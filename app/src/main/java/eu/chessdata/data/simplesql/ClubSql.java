package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;
import eu.chessdata.backend.tournamentEndpoint.model.Club;


/**
 * Created by bogda on 20/12/2015.
 */
@SimpleSQLTable(table="club",provider = "SimpleProvider")
public class ClubSql {
    @SimpleSQLColumn(value="_id", primary = true)
    public long id;

    @SimpleSQLColumn(value = "clubId")
    public Long clubId;

    @SimpleSQLColumn("name")
    public String name;

    @SimpleSQLColumn("shortName")
    public String shortName;

    @SimpleSQLColumn("email")
    public String email;

    @SimpleSQLColumn("country")
    public String country;

    @SimpleSQLColumn("city")
    public String city;

    @SimpleSQLColumn("homePage")
    public String homePage;

    @SimpleSQLColumn("description")
    public String description;

    @SimpleSQLColumn("dateCreated")
    public long dateCreated;

    @SimpleSQLColumn("updateStamp")
    public long updateStamp;

    public ClubSql(){}

    public ClubSql(eu.chessdata.backend.clubEndpoint.model.Club club){
        this.clubId = club.getClubId();
        this.name = club.getName();
        this.shortName = club.getShortName();
        this.email = club.getEmail().getEmail();
        this.country = club.getCountry();
        this.city = club.getCity();
        this.homePage = club.getHomePage().getValue();
        this.description = club.getDescription();
        this.dateCreated = club.getDateCreated();
        this.updateStamp = club.getUpdateStamp();
    }

    public ClubSql(Club club){
        this.clubId = club.getClubId();
        this.name = club.getName();
        this.shortName = club.getShortName();
        this.email = club.getEmail().getEmail();
        this.country = club.getCountry();
        this.city = club.getCity();
        this.homePage = club.getHomePage().getValue();
        this.description = club.getDescription();
        this.dateCreated = club.getDateCreated();
        this.updateStamp = club.getUpdateStamp();
    }
}
