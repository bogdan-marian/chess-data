package eu.chessdata.data.simplesql;

import android.util.Log;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;
import eu.chessdata.R;

/**
 * Created by bogdan on 07/12/2015.
 */

@SimpleSQLConfig(
        name = "SimpleProvider",
        authority = "eu.chessdata",
        database = "chess-data.db",
        version = 3
)
public class ChessProviderConfig implements ProviderConfig{
    private String TAG = "my-debug-tag";

    @Override
    public UpgradeScript[] getUpdateScripts() {
        UpgradeScript from1 = new UpgradeScript();
        from1.oldVersion = 2;
        from1.sqlScriptResource = R.raw.simplesql_updatefrom_01;

        Log.d(TAG, "Update script will remove all tables");
        return new UpgradeScript[]{from1};
    }
}
