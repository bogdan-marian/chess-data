package eu.chessdata.round;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessdata.R;
import eu.chessdata.TournamentDetailsFragment;
import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.data.simplesql.RoundTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 27/03/2016.
 */
public class RoundStateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "my-debug-tag";
    private int ROUND_STATE_FRAGMENT_LOADER;

    private String mTournamentUri;
    private int mRoundNumber;
    private ContentResolver mContentResolver;
    private int mGameCount;

    private String mTournamentId;
    private String mTournamentName;
    private FragmentManager mFragmentManager;
    private Fragment mPresenceFragment;

    public static RoundStateFragment newInstance(String stringTournamentUri, int roundNumber, FragmentManager fragmentManager) {

        RoundStateFragment fragment = new RoundStateFragment();
        Bundle args = new Bundle();

        fragment.ROUND_STATE_FRAGMENT_LOADER = 10 + roundNumber;

        args.putString(TournamentDetailsFragment.TOURNAMENT_URI, stringTournamentUri);
        args.putInt(RoundPagerFragment.ROUND_NUMBER, roundNumber);


        fragment.setArguments(args);
        fragment.mFragmentManager = fragmentManager;
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(ROUND_STATE_FRAGMENT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentResolver = getActivity().getContentResolver();
        mTournamentUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        mRoundNumber = getArguments().getInt(RoundPagerFragment.ROUND_NUMBER);

        View view = inflater.inflate(R.layout.fragment_round_state, container, false);
        TextView header = (TextView)view.findViewById(R.id.round_state_header);

        computeData();

        mPresenceFragment = RoundPresenceFragment.newInstance(mTournamentId, mRoundNumber + "", mTournamentName);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_presence_games,mPresenceFragment);
        transaction.commit();

        header.setText("Header: "+ mTournamentName+" " + mRoundNumber);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //query the round
        //CursorLoader cursorLoader = new CursorLoader(getContext(), gameUri, null, gameSelection, gameArgs, null);

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        TextView presence = (TextView) getView().findViewById(R.id.place_holder_presence);
        TextView games = (TextView) getView().findViewById(R.id.place_holder_games);
        if (mGameCount > 0) {
            //show only games
            presence.setVisibility(View.GONE);
            games.setVisibility(View.VISIBLE);
        } else {
            //show only presence
            presence.setVisibility(View.VISIBLE);
            games.setVisibility(View.GONE);


            //((HomeActivity)getActivity()).roundStateContentSwitch(R.id.fragment_container_presence_games,presenceFragment);



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



    private void computeData(){
        Uri tournamentUri = Uri.parse(mTournamentUri);
        String stringTournamentSqlId = tournamentUri.getLastPathSegment();
        Long tournamentId = MyGlobalTools.getTournamentCloudIdBySqlId(Long.parseLong(stringTournamentSqlId), mContentResolver);
        mTournamentId = tournamentId.toString();
        mTournamentName = MyGlobalTools.getTournamentNameTournamentId(tournamentId, mContentResolver);
        String roundNumber = "" + mRoundNumber;
        //Long tournamentSqlId = Long.parseLong(stringTournamentSqlId);
        //find the roundSqlId
        Log.d(TAG, "RoundStateFragment, uri = " + stringTournamentSqlId + " roundNumber = " + mRoundNumber);

        /*find if there are games data available for the round. If no games display presence
        if games then display results*/
        //locate the round
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
        Log.e(TAG, "round located: " + stringTournamentSqlId + ". " + roundNumber);
        roundCursor.moveToFirst();
        Long roundId = roundCursor.getLong(idx_roundId);
        roundCursor.close();

        //count the games
        Uri gameUri = GameTable.CONTENT_URI;
        String gameSelection = GameTable.FIELD_ROUNDID + " =?";
        String gameArgs[] = {roundId.toString()};
        Cursor gameCursor = mContentResolver.query(gameUri, null, gameSelection, gameArgs, null);
        mGameCount = gameCursor.getCount();
        gameCursor.close();
        Log.d(TAG, "End compute data, round "+ mRoundNumber );
    }
}
