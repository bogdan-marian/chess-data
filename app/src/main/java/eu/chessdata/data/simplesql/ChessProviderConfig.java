package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by bogdan on 07/12/2015.
 */

@SimpleSQLConfig(
        name = "SimpleProvider",
        authority = "eu.chessdata",
        database = "chess-data.db",
        version = 1
)
public class ChessProviderConfig implements ProviderConfig{
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
