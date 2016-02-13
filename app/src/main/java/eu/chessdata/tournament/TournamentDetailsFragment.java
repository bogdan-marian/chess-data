package eu.chessdata.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 06/02/2016.
 */
public class TournamentDetailsFragment extends Fragment {
    public static final String TOURNAMENT_URI = "tournamentDetailsFragment.tournament.uri";
    public static final String TOURNAMENT_NAME = "tournamentDetailsFragment.tournament.name";

    private String TAG = "my-debug-tag";
    private TextView mHeader;
    private ListView mListView;
    private ArrayAdapter<String> mArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String stringUri = getArguments().getString(TOURNAMENT_URI);
        String name = getArguments().getString(TOURNAMENT_NAME);
        Log.d(TAG, "Received Uri: " + stringUri);

        //set the header
        View fragmentView = inflater.inflate(R.layout.fragment_tournament_details,container,false);
        mHeader =(TextView) fragmentView.findViewById(R.id.tournament_details_header);
        mHeader.setText("Tournament: " + name);

        //set the list items
        mListView = (ListView) fragmentView.findViewById(R.id.tournament_details_list_view);
        String[]values = {"Players","Rounds","Results","Get social"};
        List<String> tournamentOptions = new ArrayList<>(Arrays.asList(values));
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
                String textItem = mArrayAdapter.getItem(position);
                Log.d(TAG,"Clicked item: " + position +" / " + textItem);
            }
        });
        return fragmentView;
    }
}
