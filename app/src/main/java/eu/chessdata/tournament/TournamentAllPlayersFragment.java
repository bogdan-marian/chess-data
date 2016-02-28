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
        String stringUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        mUri = Uri.parse(stringUri);
        Log.d(TAG, "All Players: " + mUri.toString());
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
            String sqlId = mUri.getLastPathSegment();
            PopulatePlayersList populatePlayersList = new PopulatePlayersList(getFragmentManager());
            populatePlayersList.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    class PopulatePlayersList extends AsyncTask<Void,Void,Map<String,Long>> {
        private FragmentManager mFragmentManager;

        public PopulatePlayersList(FragmentManager fragmentManager){
            mFragmentManager = fragmentManager;
        }

        @Override
        protected Map<String, Long> doInBackground(Void... params) {
            Long tournamentSqlId = Long.parseLong(mUri.getLastPathSegment());
            Log.d(TAG,"SQL ID: " + tournamentSqlId);
            Map<String,Long> map = new HashMap<>();
            map.put("Bogdan " , 1001L);
            map.put("Lacra ", 2002l);
            final String[]items = new String[map.size()];
            int i = 0;
            for (Map.Entry<String,Long> item:map.entrySet()){
                items[i++]= item.getKey();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map<String, Long> map) {
            Bundle bundle = new Bundle();
            bundle.putString(TournamentDetailsFragment.TOURNAMENT_URI,mUri.toString());

            TournamentAddPlayerFragment fragment = new TournamentAddPlayerFragment();
            fragment.setArguments(bundle);

            fragment.show(mFragmentManager, "TournamentAddPlayerFragment");
        }
    }

}
