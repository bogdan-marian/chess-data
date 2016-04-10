package eu.chessdata.round;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.data.simplesql.RoundPlayerTable;

/**
 * Created by Bogdan Oloeriu on 29/03/2016.
 */
public class RoundGamesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "my-debug-tag";
    private static final int GAMES_LOADER = 1;

    private String mTournamentId;
    private String mTournamentName;

    private String mRoundId;
    private String mRoundNumber;
    private RoundGamesAdapter mRoundGamesAdapter;
    private ContentResolver mContentResolver;

    public static RoundGamesFragment newInstance(String tournamentId, String tournamentShortName, String roundId, String roundNumber) {
        RoundGamesFragment roundGamesFragment = new RoundGamesFragment();
        roundGamesFragment.mTournamentId = tournamentId;
        roundGamesFragment.mTournamentName = tournamentShortName;
        roundGamesFragment.mRoundId = roundId;
        roundGamesFragment.mRoundNumber = roundNumber;


        return roundGamesFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(GAMES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_games, container, false);

        TextView header = (TextView) view.findViewById(R.id.round_games_header);
        header.setText("Games: (Round " + mRoundNumber + ", " + mTournamentName + ")");

        mRoundGamesAdapter = new RoundGamesAdapter(getActivity(), null, 0);
        ListView listView = (ListView) view.findViewById(R.id.round_games_list_view);
        listView.setAdapter(mRoundGamesAdapter);

        mContentResolver = getActivity().getContentResolver();

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = GameTable.CONTENT_URI;
        String selection = GameTable.FIELD_ROUNDID + " =?";
        String selectionArgs[] = {mRoundId};
        String sortOrder = GameTable.FIELD_TABLENUMBER + " ASC";

        Loader<Cursor> cursorLoader = new CursorLoader(
                getContext(),
                uri,
                null,
                selection,
                selectionArgs,
                sortOrder
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRoundGamesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRoundGamesAdapter.swapCursor(null);
    }
}
