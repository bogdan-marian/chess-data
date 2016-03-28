package eu.chessdata.round;

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

/**
 * Created by Bogdan Oloeriu on 27/03/2016.
 */
public class RoundStateFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "my-debug-tag";
    private static final int ROUND_STATE_FRAGMENT_LOADER = 2;

    private  String mTournamentUri;
    private int mRoundNumber;

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
        Long tournamentSqlId = Long.parseLong(stringTournamentSqlId);
        //find the roundSqlId
        Log.d(TAG,"RoundStateFragment, uri = " + tournamentSqlId+" roundNumber = " + mRoundNumber);

        //find if there are games data available for the round. If no games display presence
        //if games then display results



        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
