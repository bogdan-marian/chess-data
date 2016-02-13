package eu.chessdata;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ListView;

import eu.chessdata.data.simplesql.TournamentTable;

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
    private TournamentAdapter mTournamentAdapter;
    private String mProfileId;

    /**
     * A public interface that all activities containing this fragment must implement.
     * This mechanism allows activities to be notified of item selections.
     */
    public interface TournamentCallback {
        public void onTournamentItemSelected(Uri tournamentUri, String tournamentName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //notify that this fragment also needs to handle menu events
        setHasOptionsMenu(true);

        //get the profileId;
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mProfileId = sharedPreferences.getString(getString(R.string.pref_profile_profileId),"no value");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Cursor adapter will take data from our cursor and populate the ListView.
        mTournamentAdapter = new TournamentAdapter(getActivity(), null, 0);

        View fragmentView = inflater.inflate(R.layout.fragment_tournament, container, false);
        ListView listView = (ListView) fragmentView.findViewById(R.id.listView_tournament);
        listView.setAdapter(mTournamentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    int idx_id = cursor.getColumnIndex(TournamentTable.FIELD__ID);
                    int idx_name = cursor.getColumnIndex(TournamentTable.FIELD_NAME);
                    Long sqlId = cursor.getLong(idx_id);
                    String tournamentName = cursor.getString(idx_name);

                    Uri uri = TournamentTable.CONTENT_URI;
                    uri = Uri.withAppendedPath(uri,sqlId.toString());

                    ((TournamentCallback) getActivity())
                            .onTournamentItemSelected(uri, tournamentName);
                }
            }
        });
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
        if (id == R.id.action_create_tournament) {
            (new TournamentCreateFragment()).show(getFragmentManager(), "TournamentCreateFragment");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = TournamentTable.FIELD__ID + " DESC";
        Uri tournamentUri = TournamentTable.CONTENT_URI;

        return new CursorLoader(getContext(),
                tournamentUri,
                null,//projection
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTournamentAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTournamentAdapter.swapCursor(null);
    }


}