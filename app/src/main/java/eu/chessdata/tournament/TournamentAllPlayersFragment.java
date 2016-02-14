package eu.chessdata.tournament;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;
import eu.chessdata.TournamentDetailsFragment;

/**
 * Created by Bogdan Oloeriu on 14/02/2016.
 */
public class TournamentAllPlayersFragment extends Fragment {
    String TAG = "my-debug-tag";
    Uri mUri;

    public static TournamentAllPlayersFragment newInstance(String stringUri){
        TournamentAllPlayersFragment fragment = new TournamentAllPlayersFragment();
        Bundle args = new Bundle();
        args.putString(TournamentDetailsFragment.TOURNAMENT_URI, stringUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament_all_players, container, false);
        String stringUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        mUri = Uri.parse(stringUri);
        Log.d(TAG,"All Players: " + mUri.toString());
        return view;
    }
}
