package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;
import eu.chessdata.backend.profileEndpoint.model.Profile;

/**
 * Created by bogda on 07/12/2015.
 */
@SimpleSQLTable(table="profile",provider = "SimpleProvider")
public class ProfileSql {
    @SimpleSQLColumn(value="_id", primary = true)
    public long id;

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

    public  ProfileSql(){}

    public ProfileSql(Profile profile){
        this.profileId = profile.getProfileId();
        this.email = profile.getEmail().getEmail();
        this.name = profile.getName();
        this.dateOfBirth = profile.getDateOfBirth();
        this.elo = profile.getElo();
        this.dateCreated = profile.getDateCreated();
        this.updateStamp = profile.getUpdateStamp();
    }

    public ProfileSql(eu.chessdata.backend.tournamentEndpoint.model.Profile profile){
        this.profileId = profile.getProfileId();
        this.email = profile.getEmail().getEmail();
        this.name = profile.getName();
        this.dateOfBirth = profile.getDateOfBirth();
        this.elo = profile.getElo();
        this.dateCreated = profile.getDateCreated();
        this.updateStamp = profile.getUpdateStamp();
    }
}
