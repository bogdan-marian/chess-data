package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by bogda on 07/12/2015.
 */
@SimpleSQLTable(table="profile",provider = "SimpleProvider")
public class Profile {
    @SimpleSQLColumn(value="_id", primary = true)
    public long _id;

    @SimpleSQLColumn("profileId")
    public String profileId;

    @SimpleSQLColumn("email")
    public String email;

    @SimpleSQLColumn("name")
    public String name;

    @SimpleSQLColumn("dateOfBirth")
    public long dateOfBirth;

    @SimpleSQLColumn("elo")
    public int elo;

    @SimpleSQLColumn("altElo")
    public int altElo;

    @SimpleSQLColumn("dateCreated")
    public long dateCreated;

    @SimpleSQLColumn("updateStamp")
    public long updateStamp;
}
