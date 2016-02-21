package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by Bogdan Oloeriu on 21/02/2016.
 */
@SimpleSQLTable(table="TournamentPlayer",provider = "SimpleProvider")
public class TournamentPlayerSql {
    @SimpleSQLColumn(value="_id", primary = true)
    private long id;

    @SimpleSQLColumn(value = "tournamentPlayerId")
    private Long tournamentPlayerId;

    @SimpleSQLColumn(value = "tournamentId")
    private Long tournamentId;

    @SimpleSQLColumn(value = "profileId")
    private String profileId;

    @SimpleSQLColumn(value = "dateCreated")
    private long dateCreated;

    @SimpleSQLColumn(value = "updateStamp")
    private long updateStamp;

    //constructors

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

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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

    public Long getTournamentPlayerId() {
        return tournamentPlayerId;
    }

    public void setTournamentPlayerId(Long tournamentPlayerId) {
        this.tournamentPlayerId = tournamentPlayerId;
    }
}
