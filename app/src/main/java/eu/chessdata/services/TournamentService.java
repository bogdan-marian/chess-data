package eu.chessdata.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import eu.chessdata.backend.tournamentEndpoint.TournamentEndpoint;
import eu.chessdata.backend.tournamentEndpoint.model.Tournament;
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
        Intent intent = new Intent(context, TournamentService.class);
        intent.setAction(ACTION_CREATE_TOURNAMENT);
//        intent.putExtra(EXTRA_JSON_TOURNAMENT, param1);
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
            final String action = intent.getAction();
            if (ACTION_CREATE_TOURNAMENT.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_JSON_TOURNAMENT);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo("string1", "string2");
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_JSON_TOURNAMENT);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
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
}
