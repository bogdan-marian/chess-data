package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;
import eu.chessdata.backend.tournamentEndpoint.model.Tournament;

/**
 * Created by Bogdan Oloeriu on 25/01/2016.
 */
@SimpleSQLTable(table="Tournament",provider = "SimpleProvider")
public class TournamentSql {
    @SimpleSQLColumn(value="_id", primary = true)
    private long id;

    @SimpleSQLColumn(value = "tournamentId")
    private Long tournamentId;

    @SimpleSQLColumn(value = "clubId")
    private Long clubId;

    @SimpleSQLColumn(value = "name")
    private String name;

    @SimpleSQLColumn(value = "description")
    private String description;

    @SimpleSQLColumn(value = "totalRounds")
    private int totalRounds;

    @SimpleSQLColumn(value = "startDate")
    private long startDate;

    @SimpleSQLColumn(value = "endDate")
    private long endDate;

    @SimpleSQLColumn(value = "location")
    private String location;

    @SimpleSQLColumn(value = "dateCreated")
    private long dateCreated;

    @SimpleSQLColumn(value = "updateStamp")
    private long updateStamp;
    //constructors
    public TournamentSql(){}
    public TournamentSql(Tournament tournament){
        this.tournamentId = tournament.getTournamentId();
        this.clubId = tournament.getClubId();
        this.name = tournament.getName();
        this.description = tournament.getDescription();
        this.totalRounds = tournament.getTotalRounds();
        this.startDate = tournament.getStartDate();
        this.endDate = tournament.getEndDate();
        this.location = tournament.getLocation();
        this.dateCreated = tournament.getDateCreated();
        this.updateStamp = tournament.getUpdateStamp();
    }
    //getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(long updateStamp) {
        this.updateStamp = updateStamp;
    }
}
