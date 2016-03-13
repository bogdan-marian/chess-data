package eu.chessdata.tournament;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import eu.chessdata.R;
import eu.chessdata.TournamentDetailsFragment;
import eu.chessdata.data.simplesql.TournamentPlayerTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * It uses TournamentDetailsFragment.TOURNAMENT_URI to pass information
 * to itself
 * <p/>
 * Created by Bogdan Oloeriu on 14/02/2016.
 */
public class TournamentAllPlayersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    String TAG = "my-debug-tag";

    String mStringUri;
    String mName;

    private static final int ALL_PLAYERS_LOADER = 1;
    private TournamentAllPlayersAdapter mTournamentAllPlayersAdapter;
    private String mTournamentSqlId;
    private String mTournamentId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ALL_PLAYERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public static TournamentAllPlayersFragment newInstance(String stringUri, String name) {
        TournamentAllPlayersFragment fragment = new TournamentAllPlayersFragment();
        Bundle args = new Bundle();
        args.putString(TournamentDetailsFragment.TOURNAMENT_URI, stringUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mStringUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        Uri uri = Uri.parse(mStringUri);
        mTournamentSqlId = uri.getLastPathSegment();
        ContentResolver contentResolver = getContext().getContentResolver();
        Long tournamentSqlId = Long.parseLong(mTournamentSqlId);
        mTournamentId = MyGlobalTools.getTournamentCloudIdBySqlId(tournamentSqlId, contentResolver).toString();

        mName = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_NAME);
        mTournamentAllPlayersAdapter = new TournamentAllPlayersAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_tournament_all_players, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView_allPlayers);
        listView.setAdapter(mTournamentAllPlayersAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tournament_all_players_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_player) {
            dismissDialog();
            FragmentManager fragmentManager = getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(TournamentDetailsFragment.TOURNAMENT_URI, mStringUri);
            bundle.putString(TournamentDetailsFragment.TOURNAMENT_NAME, mName);

            TournamentAddPlayerFragment fragment = new TournamentAddPlayerFragment();
            fragment.setArguments(bundle);
            fragment.show(fragmentManager, "TournamentAddPlayerFragment");
            return false;
        }
        return false;
    }

    public void dismissDialog() {
        while (getFragmentManager().findFragmentByTag("TournamentAddPlayerFragment") != null) {
            Fragment prev = getFragmentManager().findFragmentByTag("TournamentAddPlayerFragment");
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TournamentPlayerTable.CONTENT_URI;

        String selection = TournamentPlayerTable.FIELD_TOURNAMENTID + " =?";
        String selectionArgs[] = {mTournamentId};
        String sortOrder = TournamentPlayerTable.FIELD_PROFILENAME + " ASC";

        Loader<Cursor> cursorLoader = new CursorLoader(getContext(),
                uri,
                null,
                selection,
                selectionArgs,
                sortOrder);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTournamentAllPlayersAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTournamentAllPlayersAdapter.swapCursor(null);
    }
}
