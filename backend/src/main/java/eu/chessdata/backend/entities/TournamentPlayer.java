package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Bogdan Oloeriu on 21/02/2016.
 */
@Entity
public class TournamentPlayer {
    @Id
    private Long tournamentPlayerId;
    @Index
    private Long tournamentId;
    @Index
    private String profileId;
    private long dateCreated;
    private long updateStamp;

    //getters and setters

    public Long getTournamentPlayerId() {
        return tournamentPlayerId;
    }

    public void setTournamentPlayerId(Long tournamentPlayerId) {
        this.tournamentPlayerId = tournamentPlayerId;
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
}
