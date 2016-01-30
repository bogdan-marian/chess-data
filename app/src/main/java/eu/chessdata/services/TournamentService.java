package eu.chessdata.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

import eu.chessdata.R;
import eu.chessdata.backend.tournamentEndpoint.TournamentEndpoint;
import eu.chessdata.backend.tournamentEndpoint.model.Tournament;
import eu.chessdata.data.simplesql.ClubTable;
import eu.chessdata.data.simplesql.TournamentSql;
import eu.chessdata.data.simplesql.TournamentTable;
import eu.chessdata.tools.MyGlobalSharedObjects;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TournamentService extends IntentService {
    private static TournamentEndpoint sTournamentEndpoint = buildTournamentEndpoint();
    private static GsonFactory sGsonFactory = new GsonFactory();

    private String TAG = "my-debug-tag";
    private SharedPreferences mSharedPreferences;
    private ContentResolver mContentResolver;
    private String mIdTokenString;
    private Long mClubEndpointId;


    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CREATE_TOURNAMENT = "eu.chessdata.services.action.ACTION_CREATE_TOURNAMENT";
    private static final String ACTION_BAZ = "eu.chessdata.services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_JSON_TOURNAMENT = "eu.chessdata.services.extra.JSON_TOURNAMENT";
    private static final String EXTRA_PARAM2 = "eu.chessdata.services.extra.PARAM2";

    public TournamentService() {
        super("TournamentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionCreateTournament(Context context, Tournament tournament) {
        String jsonTournament = serializeToJson(tournament);
        Intent intent = new Intent(context, TournamentService.class);
        intent.setAction(ACTION_CREATE_TOURNAMENT);
        intent.putExtra(EXTRA_JSON_TOURNAMENT, jsonTournament);
//        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, TournamentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_JSON_TOURNAMENT, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mSharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            mContentResolver = getContentResolver();
            mIdTokenString = mSharedPreferences.getString(
                    getString(R.string.pref_security_id_token_string), "defaultValue");

            long clubSqlId = mSharedPreferences.getLong(
                    getString(R.string.pref_managed_club_sqlId), 0L);
            mClubEndpointId = getClubEndpointId(clubSqlId);

            final String action = intent.getAction();
            if (ACTION_CREATE_TOURNAMENT.equals(action)) {
                final String jsonTournament = intent.getStringExtra(EXTRA_JSON_TOURNAMENT);
                handleActionCreateTournament(jsonTournament);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_JSON_TOURNAMENT);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the providedm
     * parameters.
     */
    private void handleActionCreateTournament(String jsonTournament) {
        Tournament tournament = deserializeFromJson(jsonTournament);

        if (tournament != null && mClubEndpointId != null) {
            try {
                //set the id
                tournament.setClubId(mClubEndpointId);
                //insert tournament in datastore
                Tournament vipTournament = sTournamentEndpoint.create(mIdTokenString, tournament)
                        .execute();
                if (vipTournament != null) {
                    //insert in sqlite
                    TournamentSql tournamentSql = new TournamentSql(vipTournament);
                    Uri newUri = mContentResolver.insert(
                            TournamentTable.CONTENT_URI, TournamentTable.getContentValues(
                                    tournamentSql, false
                            )
                    );
                    Log.d(TAG, "Tournament uri: " + newUri.toString());
                }
            } catch (IOException e) {
                Log.d(TAG, "Not able to create vipTournament from: " + tournament);
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * creates json from tournament
     *
     * @param tournament
     * @return
     */
    private static String serializeToJson(Tournament tournament) {
        try {
            String jsonTournament = sGsonFactory.toString(tournament);
            return jsonTournament;
        } catch (IOException e) {
            throw new IllegalStateException("Not able to create json");
        }
    }

    private static Tournament deserializeFromJson(String jsonString) {
        try {
            return sGsonFactory.fromString(jsonString, Tournament.class);
        } catch (IOException e) {
            throw new IllegalStateException("Not able to deserialize json");
        }
    }

    private static TournamentEndpoint buildTournamentEndpoint() {
        TournamentEndpoint.Builder builder =
                new TournamentEndpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setRootUrl(MyGlobalSharedObjects.ROOT_URL);
        return builder.build();
    }

    private Long getClubEndpointId(long clubSqlId) {
        Uri clubUri = ClubTable.CONTENT_URI;
        String[] projection = {
                ClubTable.FIELD__ID,
                ClubTable.FIELD_CLUBID
        };
        int COL_CLUBID = 1;
        String selection = ClubTable.FIELD__ID + " = ?";
        String stringClubSqlId = Long.toString(clubSqlId);
        String[] selectionArguments = {stringClubSqlId};

        Cursor cursor = mContentResolver.query(clubUri, projection, selection, selectionArguments, null);
        int count = cursor.getCount();
        cursor.moveToFirst();
        long endPointId = cursor.getLong(COL_CLUBID);
        return endPointId;
    }
}
