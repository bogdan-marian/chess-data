package eu.chessdata.round;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;
import eu.chessdata.TournamentDetailsFragment;
import eu.chessdata.data.simplesql.RoundTable;

/**
 * Created by Bogdan Oloeriu on 27/03/2016.
 */
public class RoundStateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "my-debug-tag";
    private static final int ROUND_STATE_FRAGMENT_LOADER = 2;

    private  String mTournamentUri;
    private int mRoundNumber;
    private ContentResolver mContentResolver;

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
        getLoaderManager().initLoader(ROUND_STATE_FRAGMENT_LOADER, null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentResolver = getActivity().getContentResolver();
        mTournamentUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        mRoundNumber = getArguments().getInt(RoundPagerFragment.ROUND_NUMBER);

        View view = inflater.inflate(R.layout.fragment_round_state,container,false);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //query the round
        Uri tournamentUri = Uri.parse(mTournamentUri);
        String stringTournamentSqlId = tournamentUri.getLastPathSegment();
        String roundNumber = ""+mRoundNumber;
        //Long tournamentSqlId = Long.parseLong(stringTournamentSqlId);
        //find the roundSqlId
        Log.d(TAG,"RoundStateFragment, uri = " + stringTournamentSqlId+" roundNumber = " + mRoundNumber);

        /*find if there are games data available for the round. If no games display presence
        if games then display results*/
        //locate the round
        Uri roundUri = RoundTable.CONTENT_URI;
        String roundProjection[] = {RoundTable.FIELD_ROUNDID};
        String roundSelection = RoundTable.FIELD_TOURNAMENTID +" =? and "
                + RoundTable.FIELD_ROUNDNUMBER + " =? ";

        String roundArgs[] = {stringTournamentSqlId,roundNumber};
        Cursor roundCursor = mContentResolver.query(roundUri,roundProjection,roundSelection,roundArgs,null);
        int count = roundCursor.getCount();
        if (count != 1){
            String problem = "No round data stored on the device"+stringTournamentSqlId+". " + roundNumber;
            Log.e(TAG,problem);
            throw new IllegalStateException(problem);
        }
        Log.e(TAG,"round located: "+stringTournamentSqlId+". " + roundNumber );


        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
