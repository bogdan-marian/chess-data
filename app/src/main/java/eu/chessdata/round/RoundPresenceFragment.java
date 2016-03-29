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
 * Created by Bogdan Oloeriu on 28/03/2016.
 */
public class RoundPresenceFragment extends Fragment {

    private String mTournamentId;
    private String mTournamentName;
    private String mRoundNumber;

    public static RoundPresenceFragment newInstance(String tournamentId, String roundNumber, String tournamentShortName) {
        RoundPresenceFragment roundPresenceFragment = new RoundPresenceFragment();
        roundPresenceFragment.mTournamentId = tournamentId;
        roundPresenceFragment.mTournamentName = tournamentShortName;
        roundPresenceFragment.mRoundNumber = roundNumber;

        return roundPresenceFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_presence, container, false);

        TextView header = (TextView) view.findViewById(R.id.round_presence_header);
        header.setText("Presence (Round " + mRoundNumber + ", " + mTournamentName + ")");

        return view;
    }
}
