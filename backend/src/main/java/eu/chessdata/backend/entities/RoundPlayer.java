package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Bogdan Oloeriu on 28/03/2016.
 */
@Entity
public class RoundPlayer {
    @Id
    private Long roundPlayerId;
    @Index
    private Long roundId;
    private String profileId;
    private boolean isPared;
    private long dateCreated;
    @Index
    private long updateStamp;

    //getters and setters

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
