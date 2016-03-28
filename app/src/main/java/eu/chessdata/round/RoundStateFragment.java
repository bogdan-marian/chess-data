package eu.chessdata.round;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessdata.HomeActivity;
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
    private static final int ROUND_STATE_FRAGMENT_LOADER = 2;

    private String mTournamentUri;
    private int mRoundNumber;
    private ContentResolver mContentResolver;
    private int mGameCount;

    private String mTournamentId;
    private String mTournamentName;

    public static RoundStateFragment newInstance(String stringTournamentUri, int roundNumber) {
        RoundStateFragment fragment = new RoundStateFragment();
        Bundle args = new Bundle();

        args.putString(TournamentDetailsFragment.TOURNAMENT_URI, stringTournamentUri);
        args.putInt(RoundPagerFragment.ROUND_NUMBER, roundNumber);

        fragment.setArguments(args);
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
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //query the round
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


        CursorLoader cursorLoader = new CursorLoader(getContext(), gameUri, null, gameSelection, gameArgs, null);

        RoundPresenceFragment presenceFragment = RoundPresenceFragment.newInstance(mTournamentId, mRoundNumber + "", mTournamentName);
        new ImplementFragmentTransaction().execute(presenceFragment);
        return cursorLoader;
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

    class ImplementFragmentTransaction extends AsyncTask<Fragment,Void,Void>{

        @Override
        protected Void doInBackground(Fragment... params) {
            Fragment fragment = params[0];
            ((HomeActivity)getActivity()).roundStateContentSwitch(R.id.fragment_container_presence_games, fragment);
            return null;
        }
    }
}
