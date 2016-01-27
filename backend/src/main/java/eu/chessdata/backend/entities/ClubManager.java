package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by bogda on 20/12/2015.
 */
@Entity
public class ClubManager {
    @Id
    private Long clubManagerId;

    @Index
    private String profileId;

    @Index
    private Long clubId;
    private long dateCreated;

    //constructors
    public ClubManager(){}
    public ClubManager(Long clubManagerId,
                       String profileId,
                       Long clubId,
                       long dateCreated){
        this.clubManagerId = clubManagerId;
        this.profileId = profileId;
        this.clubId = clubId;
        this.dateCreated = dateCreated;
    }

    //generated

    public Long getClubManagerId() {
        return clubManagerId;
    }

    public void setClubManagerId(Long clubManagerId) {
        this.clubManagerId = clubManagerId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
