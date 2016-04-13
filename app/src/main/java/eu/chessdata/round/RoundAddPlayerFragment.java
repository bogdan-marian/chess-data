package eu.chessdata.round;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.TournamentPlayerTable;
import eu.chessdata.services.TournamentService;

/**
 * Created by Bogdan Oloeriu on 30/03/2016.
 */
public class RoundAddPlayerFragment extends DialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor>
        , AdapterView.OnItemClickListener {

    private final String TAG = "my-debug-tag";
    private final int ROUND_ADD_PLAYER_LOADER = 0;

    private String mTournamentId;
    private String mRoundId;
    private RoundAddPlayerAdapter mAdapter;

    public static RoundAddPlayerFragment newInstance(String tournamentId, String roundId) {
        RoundAddPlayerFragment roundAddPlayerFragment = new RoundAddPlayerFragment();
        roundAddPlayerFragment.mTournamentId = tournamentId;
        roundAddPlayerFragment.mRoundId = roundId;

        return roundAddPlayerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_round_add_player, null, false);

        mAdapter = new RoundAddPlayerAdapter(getActivity(), null, 0);
        ListView listView = (ListView) view.findViewById(R.id.list_view_players);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        getDialog().getWindow().setTitle("Add player");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ROUND_ADD_PLAYER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TournamentPlayerTable.CONTENT_URI;
        String selection = TournamentPlayerTable.FIELD_TOURNAMENTID + " =?";
        String selectionArgs[] = {mTournamentId};
        String sortOrder = TournamentPlayerTable.FIELD_PROFILENAME + " ASC";

        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, null, selection, selectionArgs, sortOrder);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tournamentPlayerSqlId = String.valueOf(id);
        TournamentService.startActionCreateRoundPlayer(getContext(), mRoundId, mTournamentId, tournamentPlayerSqlId);
        this.dismiss();
    }
}
