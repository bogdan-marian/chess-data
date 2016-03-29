package eu.chessdata.round;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 29/03/2016.
 */
public class RoundGamesFragment extends Fragment {

    private String mTournamentId;
    private String mTournamentName;
    private String mRoundNumber;

    public static RoundGamesFragment newInstance(String tournamentId, String roundNumber, String tournamentShortName) {
        RoundGamesFragment roundGamesFragment = new RoundGamesFragment();
        roundGamesFragment.mTournamentId = tournamentId;
        roundGamesFragment.mTournamentName = tournamentShortName;
        roundGamesFragment.mRoundNumber = roundNumber;

        return roundGamesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_games, container, false);

        TextView header = (TextView) view.findViewById(R.id.round_games_header);
        header.setText("Games: (Round " + mRoundNumber + ", " + mTournamentName + ")");

        return view;
    }
}
