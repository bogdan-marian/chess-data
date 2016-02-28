package eu.chessdata;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bogdan Oloeriu on 06/02/2016.
 */
public class TournamentDetailsFragment extends Fragment {
    public static final String TOURNAMENT_URI = "tournamentDetailsFragment.tournament.uri";
    public static final String TOURNAMENT_NAME = "tournamentDetailsFragment.tournament.name";

    public static final int CATEGORIES = 0;
    public static final int PLAYERS = 1;
    public static final int ROUNDS = 2;
    public static final int RESULTS = 3;
    public static final int GET_SOCIAL = 4;
    /**
     * This ones are tide tot he above int final strings.
     * Consider writhing a function to initialize this from string.values file for better
     * internationalization options.
     */
    String[] mValues = {"Categories", "Players", "Rounds", "Results", "Get social"};

    private String TAG = "my-debug-tag";
    private TextView mHeader;
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;


    /**
     * A public interface for activities that contain this fragment
     */
    public interface TournamentDetailsCallback {

        /**
         * This function identifies what item was pressed inside the @TournamentDetailsFragment
         *
         * @param selection     should be CATEGORIES, PLAYERS.. etc.
         * @param tournamentUri the uri to the current tournament
         */
        public void onTournamentDetailsItemSelected(int selection, String tournamentUri, String tournamentName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String stringUri = getArguments().getString(TOURNAMENT_URI);
        final String name = getArguments().getString(TOURNAMENT_NAME);

        //set the header
        View fragmentView = inflater.inflate(R.layout.fragment_tournament_details, container, false);
        mHeader = (TextView) fragmentView.findViewById(R.id.tournament_details_header);
        mHeader.setText("Tournament: " + name);

        //set the list items
        mListView = (ListView) fragmentView.findViewById(R.id.tournament_details_list_view);

        List<String> tournamentOptions = new ArrayList<>(Arrays.asList(mValues));
        mArrayAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_tournament,
                        R.id.list_item_tournament_textView,
                        tournamentOptions
                );
        mListView.setAdapter(mArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TournamentDetailsCallback) getActivity()).onTournamentDetailsItemSelected(
                        position, stringUri, name);
            }
        });
        return fragmentView;
    }
}
