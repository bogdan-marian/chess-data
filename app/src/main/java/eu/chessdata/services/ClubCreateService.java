package eu.chessdata.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

import eu.chessdata.R;
import eu.chessdata.backend.clubEndpoint.ClubEndpoint;
import eu.chessdata.backend.clubEndpoint.model.Club;
import eu.chessdata.backend.clubEndpoint.model.ClubMember;
import eu.chessdata.data.simplesql.ClubMemberSql;
import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.data.simplesql.ClubSql;
import eu.chessdata.data.simplesql.ClubTable;
import eu.chessdata.tools.MyGlobalTools;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ClubCreateService extends IntentService {

    private static ClubEndpoint sClubEndpoint = buildClubEndpoint();
    private static GsonFactory sGsonFactory = new GsonFactory();

    private String TAG = "my-debug-tag";
    private SharedPreferences mSharedPreferences;
    private ContentResolver mContentResolver;
    private String mIdTokenString;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CREATE_CLUB = "eu.chessdata.action.CREATE_CLUB";
    private static final String ACTION_BAZ = "eu.chessdata.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_JSON_CLUB = "eu.chessdata.extra.JSON.CLUB";
    private static final String EXTRA_SHORT_NAME = "eu.chessdata.extra.SHORT_NAME";

    public ClubCreateService() {
        super("ClubCreateService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionCreateClub(Context context, Club club) {
        String jsonClub = serializeToJson(club);
        Intent intent = new Intent(context, ClubCreateService.class);
        intent.setAction(ACTION_CREATE_CLUB);
        intent.putExtra(EXTRA_JSON_CLUB, jsonClub);
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
        Intent intent = new Intent(context, ClubCreateService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_JSON_CLUB, param1);
        intent.putExtra(EXTRA_SHORT_NAME, param2);
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

            final String action = intent.getAction();
            if (ACTION_CREATE_CLUB.equals(action)) {
                final String jsonClub = intent.getStringExtra(EXTRA_JSON_CLUB);
                handleActionCreateClub(jsonClub);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_JSON_CLUB);
                final String param2 = intent.getStringExtra(EXTRA_SHORT_NAME);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCreateClub(String jsonClub) {
        Log.d(TAG, "JSON.CLUB: " + jsonClub);
        Club club = deserializeFromJson(jsonClub);
        if (club == null) return;
        Log.d(TAG, "Club is not null");
        try {
            //insert club in datastore
            Club vipClub = sClubEndpoint.create(mIdTokenString, club).execute();
            if (vipClub == null) return;

            //insert club in sqlite
            Uri newUri = mContentResolver.insert(
                    ClubTable.CONTENT_URI, ClubTable.getContentValues(
                            new ClubSql(vipClub), false));
            Log.d(TAG, "Uri new sql club: " + newUri.toString());

            //get the clubMember in a loop until you succeed.
            while (true){
                ClubMember vipClubMember = sClubEndpoint.getFirstManager(mIdTokenString,vipClub.getClubId())
                        .execute();
                String illegalRequest = vipClubMember.getProfileId().split(":")[0];
                if (illegalRequest.equals("Illegal request")) {
                    Log.d(TAG, "From server with love: " + vipClubMember.getProfileId());
                    Thread.sleep(3000L);
                    continue;
                }

                //vipClubMember retrieved from server side
                Uri memberUri = mContentResolver.insert(
                        ClubMemberTable.CONTENT_URI,
                        ClubMemberTable.getContentValues(
                                new ClubMemberSql(vipClubMember), false
                        )
                );
                Log.d(TAG, "New member uri: " + memberUri.toString());
                break;
            }

            //change also the default Managed club
            String clubName = vipClub.getName();
            Long clubId = ContentUris.parseId(newUri);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(getString(R.string.pref_managed_club_name), clubName);
            editor.putLong(getString(R.string.pref_managed_club_sqlId), clubId);
            editor.commit();
            Log.d(TAG, "Allso changed the default managed club");

        } catch (IOException e) {
            Log.d(TAG, "Not able to create vipClub from: " + club);
        } catch (InterruptedException e) {
            Log.d(TAG, "Sleep was interrupted: " + club);
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
     * In creates a Json string that represents the club
     *
     * @param club
     * @return
     */
    private static String serializeToJson(Club club) {
        try {
            String jsonClub = sGsonFactory.toString(club);
            return jsonClub;
        } catch (IOException e) {
            throw new IllegalStateException("Not able to create json");
        }
    }

    /**
     * It decodes the json club and creates a club from it.
     *
     * @param jsonString
     * @return
     */
    private static Club deserializeFromJson(String jsonString) {
        try {
            return sGsonFactory.fromString(jsonString, Club.class);
        } catch (IOException e) {
            throw new IllegalStateException("Not able to deserialize json");
        }
    }

    private static ClubEndpoint buildClubEndpoint() {
        ClubEndpoint.Builder builder =
                new ClubEndpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setRootUrl(MyGlobalTools.ROOT_URL);
        return builder.build();
    }
}
