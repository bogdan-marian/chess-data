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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.RoundPlayerTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 28/03/2016.
 */
public class RoundPresenceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "my-debug-tag";
    private static final int PRESENCE_LOADER = 0;

    private String mTournamentId;
    private String mTournamentName;
    private String mRoundId;
    private String mRoundNumber;

    private RoundPresenceAdapter mRoundPresenceAdapter;
    private ContentResolver mContentResolver;

    public static RoundPresenceFragment newInstance(String tournamentId, String tournamentShortName, String roundId, String roundNumber) {
        RoundPresenceFragment roundPresenceFragment = new RoundPresenceFragment();
        roundPresenceFragment.mTournamentId = tournamentId;
        roundPresenceFragment.mTournamentName = tournamentShortName;
        roundPresenceFragment.mRoundId = roundId;
        roundPresenceFragment.mRoundNumber = roundNumber;

        return roundPresenceFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(PRESENCE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_round_presence, container, false);

        TextView header = (TextView) view.findViewById(R.id.round_presence_header);
        header.setText("Presence (Round " + mRoundNumber + ", " + mTournamentName + ")");

        Button button = (Button) view.findViewById(R.id.presence_add_player);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add for roundNr: " + mRoundNumber);
                RoundAddPlayerFragment addPlayerFragment = RoundAddPlayerFragment.newInstance(mTournamentId, mRoundId);
                addPlayerFragment.show(getFragmentManager(), "RoundAddPlayerFragment");
            }
        });

        Button create = (Button) view.findViewById(R.id.presence_creage_games);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoundStateFragment roundStateFragment = (RoundStateFragment) getParentFragment();
                CreateGamesDialog createGamesDialog = CreateGamesDialog.newInstance(mRoundId, roundStateFragment);
                createGamesDialog.show(getFragmentManager(), "CreateGamesDialog");
            }
        });

        mRoundPresenceAdapter = new RoundPresenceAdapter(getActivity(), null, 0);
        ListView listView = (ListView) view.findViewById(R.id.round_presence_list_view);
        listView.setAdapter(mRoundPresenceAdapter);

        mContentResolver = getActivity().getContentResolver();

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = RoundPlayerTable.CONTENT_URI;

        String selection = RoundPlayerTable.FIELD_ROUNDID + " =?";
        String selectionArgs[] = {mRoundId};
        String sortOrder = RoundPlayerTable.FIELD_PROFILENAME + " ASC";

        Loader<Cursor> cursorLoader = new CursorLoader(getContext(), uri, null, selection, selectionArgs, sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRoundPresenceAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRoundPresenceAdapter.swapCursor(null);
    }
}
