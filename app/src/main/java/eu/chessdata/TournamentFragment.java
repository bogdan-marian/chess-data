package eu.chessdata;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class TournamentFragment extends Fragment {
    private String TAG = "my-debug-tag";
    private ArrayAdapter<String> mTournamentsAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String [] items = {
                "Crerel junior winter 2015",
                "Crerel Junior Spring 2016",
                "Tournament 1",
                "Tournament 2",
                "Tournament 3",
                "Tournament 4",
                "Tournament 5",
                "Tournament 6",
                "Tournament 7",
                "Tournament 8",
                "Tournament 9",
                "Tournament 10",
        };
        List<String> tournaments = Arrays.asList(items);

        mTournamentsAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_tournament,
                        R.id.list_item_tournament_textView,
                        tournaments);

        View fragmentView = inflater.inflate(R.layout.fragment_tournament,container,false);
        ListView listView = (ListView)fragmentView.findViewById(R.id.listView_tournament);
        listView.setAdapter(mTournamentsAdapter);
        return fragmentView;
    }
}