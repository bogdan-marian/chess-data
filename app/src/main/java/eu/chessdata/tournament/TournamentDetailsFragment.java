package eu.chessdata.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 06/02/2016.
 */
public class TournamentDetailsFragment extends Fragment {
    private String TAG = "my-debug-tag";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tournament_details,container,false);
        return fragmentView;
    }
}
