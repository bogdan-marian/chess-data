package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by Bogdan Oloeriu on 28/03/2016.
 */
@SimpleSQLTable(table = "RoundPlayer", provider = "SimpleProvider")
public class RoundPlayerSql {
    @SimpleSQLColumn(value = "_id", primary = true)
    private long id;

    @SimpleSQLColumn(value = "")
    private Long roundPlayerId;

    @SimpleSQLColumn(value = "")
    private Long roundId;

    @SimpleSQLColumn(value = "")
    private String profileId;

    @SimpleSQLColumn(value = "")
    private boolean isPared;

    @SimpleSQLColumn(value = "")
    private long dateCreated;

    @SimpleSQLColumn(value = "")
    private long updateStamp;

    //constructors

    public RoundPlayerSql() {
    }

    //getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getRoundPlayerId() {
        return roundPlayerId;
    }

    public void setRoundPlayerId(Long roundPlayerId) {
        this.roundPlayerId = roundPlayerId;
    }

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public boolean isPared() {
        return isPared;
    }

    public void setIsPared(boolean isPared) {
        this.isPared = isPared;
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
