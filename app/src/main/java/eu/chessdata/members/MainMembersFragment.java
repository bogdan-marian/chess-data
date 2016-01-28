package eu.chessdata.members;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;

/**
 * This is the fragment that lists all the chess players that somehow are affiliated to the club
 * club members
 * club quests
 * virtual players
 *
 * Created by Bogdan Oloeriu on 28/01/2016.
 */
public class MainMembersFragment extends Fragment{
    private String TAG = "my-debug-tag";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main_members,container,false);
        return fragmentView;
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
}
