package eu.chessdata.round;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;
import eu.chessdata.TournamentDetailsFragment;
import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.data.simplesql.RoundTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 27/03/2016.
 */
public class RoundStateFragment extends Fragment {
    private static final String TAG = "my-debug-tag";
    private int ROUND_STATE_FRAGMENT_LOADER;

    private View mView;

    private String mTournamentUri;
    private int mRoundNumber;
    private ContentResolver mContentResolver;
    private int mGameCount;

    private String mTournamentId;
    private String mTournamentName;
    private String mRoundId;

    public static RoundStateFragment newInstance(String stringTournamentUri, int roundNumber, FragmentManager fragmentManager) {

        RoundStateFragment fragment = new RoundStateFragment();
        Bundle args = new Bundle();

        fragment.ROUND_STATE_FRAGMENT_LOADER = 10 + roundNumber;

        args.putString(TournamentDetailsFragment.TOURNAMENT_URI, stringTournamentUri);
        args.putInt(RoundPagerFragment.ROUND_NUMBER, roundNumber);


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //getLoaderManager().initLoader(ROUND_STATE_FRAGMENT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentResolver = getActivity().getContentResolver();
        mTournamentUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        mRoundNumber = getArguments().getInt(RoundPagerFragment.ROUND_NUMBER);
        mView = inflater.inflate(R.layout.fragment_round_state, container, false);

        computeData();
        configureVisibility();

        return mView;
    }


    private void computeData() {
        Uri tournamentUri = Uri.parse(mTournamentUri);
        String stringTournamentSqlId = tournamentUri.getLastPathSegment();
        Long tournamentId = MyGlobalTools.getTournamentCloudIdBySqlId(Long.parseLong(stringTournamentSqlId), mContentResolver);
        mTournamentId = tournamentId.toString();
        mTournamentName = MyGlobalTools.getTournamentNameTournamentId(tournamentId, mContentResolver);
        String roundNumber = "" + mRoundNumber;

        Uri roundUri = RoundTable.CONTENT_URI;
        String roundProjection[] = {RoundTable.FIELD_ROUNDID};
        int idx_roundId = 0;
        String roundSelection = RoundTable.FIELD_TOURNAMENTID + " =? and "
                + RoundTable.FIELD_ROUNDNUMBER + " =? ";

        String roundArgs[] = {tournamentId.toString(), roundNumber};
        Cursor roundCursor = mContentResolver.query(roundUri, roundProjection, roundSelection, roundArgs, null);
        int count = roundCursor.getCount();

        if (count != 1) {
            String problem = "No round data stored on the device" + stringTournamentSqlId + ". " + roundNumber;
            Log.e(TAG, problem);
            throw new IllegalStateException(problem);
        }
        roundCursor.moveToFirst();
        Long roundId = roundCursor.getLong(idx_roundId);
        roundCursor.close();
        mRoundId = roundId.toString();

        //count the games
        Uri gameUri = GameTable.CONTENT_URI;
        String gameSelection = GameTable.FIELD_ROUNDID + " =?";
        String gameArgs[] = {roundId.toString()};
        Cursor gameCursor = mContentResolver.query(gameUri, null, gameSelection, gameArgs, null);
        mGameCount = gameCursor.getCount();
        gameCursor.close();
    }

    private void configureVisibility() {
        if (mGameCount == 0) {
            showPresence();
        } else {
            showGames();
        }
    }

    protected void showPresence() {
        String roundNumber = mRoundNumber + "";
        RoundPresenceFragment mPresenceFragment = RoundPresenceFragment.newInstance(mTournamentId, mTournamentName, mRoundId, roundNumber);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_presence_games, mPresenceFragment);
        transaction.commit();
    }

    protected void showGames() {
        String roundNumber = mRoundNumber + "";
        RoundGamesFragment gamesFragment = RoundGamesFragment.newInstance(mTournamentId, mTournamentName, mRoundId, roundNumber);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_presence_games, gamesFragment);
        transaction.commit();
    }
}
