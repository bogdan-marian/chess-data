package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by bogda on 20/12/2015.
 */
@Entity
public class ClubMember {
    @Id
    private Long clubMemberId;
    @Index private String profileId;
    @Index private Long clubId;
    private boolean guestProfile;
    private boolean managerProfile;
    @Index boolean archived = false;
    private long dateCreated;
    private long updateStamp;

    //constructors
    public ClubMember(){}
    public ClubMember(Long clubMemberId,
                      String profileId,
                      Long virtualProfileId,
                      Long clubId,
                      long dateCreated,
                      long updateStamp){
        this.clubMemberId = clubMemberId;
        this.profileId = profileId;
        this.clubId = clubId;
        this.dateCreated = dateCreated;
        this.updateStamp = updateStamp;
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


    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public long getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(long updateStamp) {
        this.updateStamp = updateStamp;
    }

    public boolean isGuestProfile() {
        return guestProfile;
    }

    public void setGuestProfile(boolean guestProfile) {
        this.guestProfile = guestProfile;
    }

    public boolean isManagerProfile() {
        return managerProfile;
    }

    public void setManagerProfile(boolean managerProfile) {
        this.managerProfile = managerProfile;
    }
}
