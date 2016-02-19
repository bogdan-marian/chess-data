package eu.chessdata.tournament;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import eu.chessdata.R;

/**
 * Created by Bogdan Oloeriu on 10/02/2016.
 */
public class TournamentAddPlayerFragment extends DialogFragment implements AdapterView.OnItemClickListener{
    private String TAG = "my-debug-tag";
    private AlertDialog mAlertDialog;
    private ListView mListView;

    String[] tempItems = {"item a","item b", "item 3", "item 4","item 5","item 6","item 7","item 8","item 9"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament_add_player,null,false);
        mListView = (ListView)view.findViewById(R.id.list_view_players);

        getDialog().getWindow().setTitle("Select player");

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_text,tempItems);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        Toast.makeText(getActivity(),tempItems[position],Toast.LENGTH_SHORT).show();
    }
}
