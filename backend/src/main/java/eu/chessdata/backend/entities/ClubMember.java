package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by bogda on 20/12/2015.
 */
@Entity
public class ClubMember {
    @Id
    private Long clubMemberId;
    private String profileId;
    private Long clubId;
    private long dateCreated;

    //constructors
    public ClubMember(){}
    public ClubMember(Long clubMemberId,
                      String profileId,
                      Long clubId,
                      long dateCreated){
        this.clubMemberId = clubMemberId;
        this.profileId = profileId;
        this.clubId = clubId;
        this.dateCreated = dateCreated;
    }

    //generated

    public Long getClubMemberId() {
        return clubMemberId;
    }

    public void setClubMemberId(Long clubMemberId) {
        this.clubMemberId = clubMemberId;
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
