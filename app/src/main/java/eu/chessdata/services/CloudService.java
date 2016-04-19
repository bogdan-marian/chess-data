package eu.chessdata.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.DeviceToCloudSql;
import eu.chessdata.data.simplesql.DeviceToCloudTable;
import eu.chessdata.data.simplesql.GameSql;
import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.tools.MyGlobalTools;


public class CloudService extends IntentService {
    private String TAG = "my-debug-tag";

    private SharedPreferences mSharedPreferences;
    private String mIdTokenString;
    private String mProfileId;

    private static final String ACTION_DEVICE_TO_CLOUD = "eu.chessdata.services.ACTION_DEVICE_TO_CLOUD";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "eu.chessdata.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "eu.chessdata.services.extra.PARAM2";

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
        Cursor cursor = getContentResolver().query(uri,null,selection,selectionArgs,null);
        cursor.moveToFirst();
        GameSql gameSql = GameTable.getRow(cursor,true);
        //todo make sure that wee also update the game timeStamp when updating the result
        //todo create tournamentEndpoint gameUpdate(idTokenString, game)

    }
}
