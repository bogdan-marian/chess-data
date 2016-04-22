package eu.chessdata.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

import eu.chessdata.R;
import eu.chessdata.backend.tournamentEndpoint.TournamentEndpoint;
import eu.chessdata.backend.tournamentEndpoint.model.Game;
import eu.chessdata.data.simplesql.DeviceToCloudSql;
import eu.chessdata.data.simplesql.DeviceToCloudTable;
import eu.chessdata.data.simplesql.GameSql;
import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.tools.MyGlobalTools;


public class CloudService extends IntentService {
    private String TAG = "my-debug-tag";
    private static TournamentEndpoint tournamentEndpoint = buildTournamentEndpoint();

    private SharedPreferences mSharedPreferences;
    private String mIdTokenString;
    private String mProfileId;

    private static final String ACTION_DEVICE_TO_CLOUD = "eu.chessdata.services.ACTION_DEVICE_TO_CLOUD";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "eu.chessdata.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "eu.chessdata.services.extra.PARAM2";

    private static TournamentEndpoint buildTournamentEndpoint() {
        TournamentEndpoint.Builder builder =
                new TournamentEndpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setRootUrl(MyGlobalTools.ROOT_URL);
        return builder.build();
    }

    public CloudService() {
        super("CloudService");
    }


    public static void startActionDeviceToCloud(Context context) {
        Intent intent = new Intent(context, CloudService.class);
        intent.setAction(ACTION_DEVICE_TO_CLOUD);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mSharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        mIdTokenString = mSharedPreferences.getString(getString(R.string.pref_security_id_token_string), "defaultValue");
        mProfileId = mSharedPreferences.getString(getString(R.string.pref_profile_profileId), "defaultValue");

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DEVICE_TO_CLOUD.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionDeviceToCloud();
            }
        }
    }

    private void handleActionDeviceToCloud() {
        Log.d(TAG, "ok start of handleActionDeviceToCloud");
        //select all items from DeviceToCloud table
        Cursor cursor = getContentResolver().query(DeviceToCloudTable.CONTENT_URI, null, null, null, null);
        List<DeviceToCloudSql> items = DeviceToCloudTable.getRows(cursor, true);
        for (DeviceToCloudSql item : items) {
            MyGlobalTools.Table table = MyGlobalTools.Table.valueOf(item.getTableName());
            switch (table) {
                case GAME:
                    sendAndUpdateGame(item);
                    break;
            }
        }
    }

    private void sendAndUpdateGame(DeviceToCloudSql item) {
        //select the game
        Uri uri = GameTable.CONTENT_URI;
        String selection = GameTable.FIELD__ID + " =?";
        String[] selectionArgs = {String.valueOf(item.getTableId())};
        Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);
        cursor.moveToFirst();
        GameSql gameSql = GameTable.getRow(cursor, true);

        Game game = new Game();
        game.setGameId(gameSql.getGameId());
        game.setRoundId(gameSql.getRoundId());
        game.setTableNumber(gameSql.getTableNumber());
        game.setWhitePlayerId(gameSql.getWhitePlayerId());
        game.setBlackPlayerId(gameSql.getBlackPlayerId());
        game.setResult(gameSql.getResult());
        game.setUpdateStamp(gameSql.getUpdateStamp());

        try {
            Game vipGame = tournamentEndpoint.gameUpdate(mIdTokenString, game).execute();
            String hackMessage = vipGame.getWhitePlayerId();
            String message = hackMessage.split(":")[0];
            if (message.equals("No update")) {
                Log.d(TAG, "Something happened: " + hackMessage);
                throw new IllegalStateException(message);
            }
            long localStamp = game.getUpdateStamp();
            long cloudStamp = vipGame.getUpdateStamp();
            if (localStamp < cloudStamp) {
                //update the current game
                GameSql newGameSql = new GameSql(vipGame);
                ContentValues contentValues = GameTable.getContentValues(gameSql, false);
                contentValues.put(GameTable.FIELD__ID, gameSql.getId());
                int rowsUpdated = getContentResolver().update(
                        uri, contentValues, selection, selectionArgs
                );
                if (rowsUpdated != 1){
                    String problem = "You should update only 1 item";
                    Log.e(TAG,problem);
                    throw new IllegalStateException(problem);
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "Not able to sendAndUpdateGame: " + e);
        }
    }


}
