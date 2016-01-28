package eu.chessdata.members;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main_members,container,false);
        return fragmentView;
    }
}
