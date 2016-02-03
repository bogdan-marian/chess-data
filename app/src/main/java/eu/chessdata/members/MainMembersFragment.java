package eu.chessdata.members;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import eu.chessdata.R;
import eu.chessdata.TournamentAdapter;
import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.data.simplesql.TournamentTable;

/**
 * This is the fragment that lists all the chess players that somehow are affiliated to the club
 * club members
 * club quests
 * virtual players
 *
 * Created by Bogdan Oloeriu on 28/01/2016.
 */
public class MainMembersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private String TAG = "my-debug-tag";

    private static final int MAIN_MEMBERS_LOADER = 1;
    private ClubMembersAdapter mClubMembersAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //set the addapter
        mClubMembersAdapter = new ClubMembersAdapter(getActivity(),null,0);

        View fragmentView = inflater.inflate(R.layout.fragment_main_members,container,false);
        ListView listView = (ListView) fragmentView.findViewById(R.id.listView_member);
        listView.setAdapter(mClubMembersAdapter);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MAIN_MEMBERS_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_members_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create_virtual_profile){
            VirtualProfileCreateFragment virtualProfileCreateFragment =
                    new VirtualProfileCreateFragment();
            virtualProfileCreateFragment.show(getFragmentManager(),"VirtualProfileCreateFragment");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri membersUri = ClubMemberTable.CONTENT_URI;
        String sortOrder = ClubMemberTable.FIELD__ID + " DESC";
        return new CursorLoader(getContext(),
                membersUri,
                null,//projection
                null,
                null,
                sortOrder );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mClubMembersAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mClubMembersAdapter.swapCursor(null);
    }
}
