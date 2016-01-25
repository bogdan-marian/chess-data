package eu.chessdata;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import eu.chessdata.data.simplesql.ClubTable;

/**
 * some useful imports
 * <p/>
 * import android.support.v4.app.Fragment;
 * import android.support.v4.app.LoaderManager;
 * import android.support.v4.content.CursorLoader;
 * import android.support.v4.content.Loader;
 */

public class TournamentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String TAG = "my-debug-tag";

    private static final int TOURNAMENT_LOADER = 0;
    private ClubAdapter mClubAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //notify that this fragment also needs to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Cursor adapter will take data from our cursor and populate the ListView.
        mClubAdapter = new ClubAdapter(getActivity(), null, 0);

        View fragmentView = inflater.inflate(R.layout.fragment_tournament, container, false);
        ListView listView = (ListView) fragmentView.findViewById(R.id.listView_tournament);
        listView.setAdapter(mClubAdapter);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TOURNAMENT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tournament_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create_tournament){
            (new TournamentCreateFragment()).show(getFragmentManager(),"TournamentCreateFragment");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ClubTable.FIELD__ID + " DESC";
        Uri clubUri = ClubTable.CONTENT_URI;

        return new CursorLoader(getContext(),
                clubUri,
                null,//projection
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mClubAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mClubAdapter.swapCursor(null);
    }
}