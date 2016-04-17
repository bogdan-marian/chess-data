package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by Bogdan Oloeriu on 17/04/2016.
 */
@SimpleSQLTable(table = "DeviceToCloud", provider = "SimpleProvider")
public class DeviceToCloudSql {
    @SimpleSQLColumn(value = "_id", primary = true)
    private long id;

    @SimpleSQLColumn(value = "tableName")
    private String tableName;

    @SimpleSQLColumn(value = "archived")
    private boolean archived = false;

    //constructors
    public DeviceToCloudSql(){
    }
    //special getters
    public boolean getArchived(){
        return this.archived;
    }

    //getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
