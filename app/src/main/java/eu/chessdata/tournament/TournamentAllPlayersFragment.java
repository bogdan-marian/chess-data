package eu.chessdata.tournament;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import eu.chessdata.R;
import eu.chessdata.TournamentDetailsFragment;

/**
 * It uses TournamentDetailsFragment.TOURNAMENT_URI to pass information
 * to itself
 *
 * Created by Bogdan Oloeriu on 14/02/2016.
 */
public class TournamentAllPlayersFragment extends Fragment {
    String TAG = "my-debug-tag";
    Uri mUri;
    String mStringUri;
    String mName;




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
        View view = inflater.inflate(R.layout.fragment_tournament_all_players, container, false);
        mStringUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        mName = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_NAME);
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
            FragmentManager fragmentManager = getFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString(TournamentDetailsFragment.TOURNAMENT_URI,mStringUri);
            bundle.putString(TournamentDetailsFragment.TOURNAMENT_NAME,mName);

            TournamentAddPlayerFragment fragment = new TournamentAddPlayerFragment();
            fragment.setArguments(bundle);
            fragment.show(fragmentManager,"TournamentAddPlayerFragment");
        }
        return super.onOptionsItemSelected(item);
    }


}
