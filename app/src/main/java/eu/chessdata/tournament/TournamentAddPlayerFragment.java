package eu.chessdata.tournament;

import android.content.ContentResolver;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.ProfileTable;
import eu.chessdata.data.simplesql.TournamentPlayerTable;
import eu.chessdata.tools.MyGlobalSharedObjects;

/**
 * Created by Bogdan Oloeriu on 10/02/2016.
 */
public class TournamentAddPlayerFragment extends DialogFragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private String TAG = "my-debug-tag";
    private AlertDialog mAlertDialog;
    private ListView mListView;
    private static final int ADD_PLAYER_LOADER = 0;
    private TournamentAddPlayerAdapter mAdapter;
    private ContentResolver mContentResolver;

    //String[] tempItems = {"item a","item b", "item 3", "item 4","item 5","item 6","item 7","item 8","item 9"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentResolver = getActivity().getContentResolver();
        mAdapter = new TournamentAddPlayerAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_tournament_add_player, null, false);
        mListView = (ListView) view.findViewById(R.id.list_view_players);
        mListView.setAdapter(mAdapter);
        getDialog().getWindow().setTitle("Select player");

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(ADD_PLAYER_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
//        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_text,tempItems);
//        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        //Toast.makeText(getActivity(),tempItems[position],Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "On todo for: " + position + "/" + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //TODO insert bundle arguments and log them for debug
        Cursor cursor = mContentResolver.query(
                ProfileTable.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        int idx_profileSqlId = cursor.getColumnIndex(ProfileTable.FIELD__ID);
        int idx_profileName = cursor.getColumnIndex(ProfileTable.FIELD_NAME);
        Map<Long,String>profileMap = new HashMap<>();
        while (cursor.moveToNext()){
            long profileId = cursor.getLong(idx_profileSqlId);
            String name = cursor.getString(idx_profileName);
            profileMap.put(profileId,name);
        }
        cursor.close();
        MyGlobalSharedObjects.profileNames = profileMap;

        Uri tournamentPlay;
        return new CursorLoader(getContext(),
                ProfileTable.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
