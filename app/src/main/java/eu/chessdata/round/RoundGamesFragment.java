package eu.chessdata.round;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.data.simplesql.RoundPlayerTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 29/03/2016.
 */
public class RoundGamesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "my-debug-tag";
    private static final int GAMES_LOADER = 1;
    private static final int NOT_PAIRED_LOADER = 2;

    private String mTournamentId;
    private String mTournamentName;

    private String mRoundId;
    private String mRoundNumber;
    private RoundGamesAdapter mRoundGamesAdapter;
    private RoundNotPairedAdapter mRoundNotPairedAdapter;
    private ContentResolver mContentResolver;

    public static RoundGamesFragment newInstance(String tournamentId, String tournamentShortName, String roundId, String roundNumber) {
        RoundGamesFragment roundGamesFragment = new RoundGamesFragment();
        roundGamesFragment.mTournamentId = tournamentId;
        roundGamesFragment.mTournamentName = tournamentShortName;
        roundGamesFragment.mRoundId = roundId;
        roundGamesFragment.mRoundNumber = roundNumber;

        return roundGamesFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(GAMES_LOADER, null, this);
        getLoaderManager().initLoader(NOT_PAIRED_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_games, container, false);

        TextView header = (TextView) view.findViewById(R.id.round_games_header);
        header.setText("Games: (Round " + mRoundNumber + ", " + mTournamentName + ")");

        mRoundGamesAdapter = new RoundGamesAdapter(getActivity(), null, 0);
        ListView listView = (ListView) view.findViewById(R.id.round_games_list_view);
        listView.setAdapter(mRoundGamesAdapter);

        mRoundNotPairedAdapter = new RoundNotPairedAdapter(getActivity(),null,0);
        ListView notParedList = (ListView) view.findViewById(R.id.round_games_not_paired_list_view);
        notParedList.setAdapter(mRoundNotPairedAdapter);

        mContentResolver = getActivity().getContentResolver();

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = GameTable.CONTENT_URI;
        String selection = GameTable.FIELD_ROUNDID + " =?";
        String selectionArgs[] = {mRoundId};
        String sortOrder = GameTable.FIELD_TABLENUMBER + " ASC";

        Loader<Cursor> gamesLoader = new CursorLoader(
                getContext(),
                uri,
                null,
                selection,
                selectionArgs,
                sortOrder
        );

        Loader<Cursor> notParedLoader = null;
        if (mRoundId != null) {
            String[] projection = {
                    GameTable.FIELD_WHITEPLAYERID,
                    GameTable.FIELD_BLACKPLAYERID};
            int idx_whiteId = 0;
            int idx_blackId = 1;
            Log.d(TAG, "roundId = " + mRoundId);
            Cursor games = mContentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

            List<String> paredItems = new ArrayList<>();
            while (games.moveToNext()) {
                String whiteId = games.getString(idx_whiteId);
                if (whiteId != null) {
                    paredItems.add(whiteId);
                }
                String blackId = games.getString(idx_blackId);
                if (blackId != null) {
                    paredItems.add(blackId);
                }
            }
            games.close();

            List<String> names = new ArrayList<>();
            for (String idVal : paredItems) {
                names.add(MyGlobalTools.getNameByProfileId(idVal));
            }
            Log.d(TAG, "pared list: " + names);

            Uri uri2 = RoundPlayerTable.CONTENT_URI;
            String[] projection2 = {RoundPlayerTable.FIELD_PROFILEID,
                    RoundPlayerTable.FIELD_PROFILENAME};
            int idx_profileId = 0;
            StringBuilder sb = new StringBuilder();
            sb.append(RoundPlayerTable.FIELD_PROFILEID + " NOT IN (");
            boolean firstItem = true;
            for (String item : paredItems) {
                if (firstItem) {
                    firstItem = false;
                    sb.append(" ?");
                } else {
                    sb.append(", ?");
                }
            }
            sb.append(") ");
            sb.append("AND " + RoundPlayerTable.FIELD_ROUNDID + " =?");
            String selection2 = sb.toString();
            String sortOrder2 = RoundPlayerTable.FIELD_PROFILENAME + " ASC";
            //add also the id of the round to the pareItems
            paredItems.add(mRoundId);
            String selectionArgs2[] = new String[paredItems.size()];
            paredItems.toArray(selectionArgs2);
            Cursor cursor2 = mContentResolver.query(uri2, projection2, selection2, selectionArgs2, null);
            List<String> notParedItems = new ArrayList<>();
            while (cursor2.moveToNext()) {
                notParedItems.add(cursor2.getString(idx_profileId));
            }
            cursor2.close();
            names = new ArrayList<>();
            for (String item : notParedItems) {
                names.add(MyGlobalTools.getNameByProfileId(item));
            }
            Log.d(TAG, "not pared: " + names);

            notParedLoader = new CursorLoader(
                    getContext(),
                    uri2,
                    null,
                    selection2,
                    selectionArgs2,
                    sortOrder2
            );
        }

        if (id == GAMES_LOADER) {
            return gamesLoader;
        } else if (id == NOT_PAIRED_LOADER) {
            return notParedLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == GAMES_LOADER) {
            mRoundGamesAdapter.swapCursor(data);
        }else if (id == NOT_PAIRED_LOADER){
            mRoundNotPairedAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();
        switch (id){
            case GAMES_LOADER:
                mRoundGamesAdapter.swapCursor(null);
                break;
            case NOT_PAIRED_LOADER:
                mRoundNotPairedAdapter.swapCursor(null);
                break;
        }
    }
}
