package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;
import eu.chessdata.backend.clubEndpoint.model.ClubMember;

/**
 * Created by Bogdan Oloeriu on 28/01/2016.
 */
@SimpleSQLTable(table="clubMember",provider = "SimpleProvider")
public class ClubMemberSql {
    @SimpleSQLColumn(value="_id", primary = true)
    public long id;

    @SimpleSQLColumn("clubMemberId")
    private Long clubMemberId;

    @SimpleSQLColumn("profileId")
    private String profileId;

    @SimpleSQLColumn("clubId")
    private Long clubId;

    @SimpleSQLColumn("guestProfile")
    boolean guestProfile;

    @SimpleSQLColumn("managerProfile")
    boolean managerProfile;

    @SimpleSQLColumn("archived")
    private boolean archived = false;

    @SimpleSQLColumn("dateCreated")
    private long dateCreated;

    @SimpleSQLColumn("updateStamp")
    private long updateStamp;

    //constructors
    public ClubMemberSql(){}
    public ClubMemberSql(ClubMember clubMember){
        this.clubMemberId = clubMember.getClubMemberId();
        this.profileId = clubMember.getProfileId();
        this.clubId = clubMember.getClubId();
        this.guestProfile = clubMember.getGuestProfile();
        this.managerProfile = clubMember.getManagerProfile();
        this.archived = clubMember.getArchived();
        this.dateCreated = clubMember.getDateCreated();
        this.updateStamp = clubMember.getUpdateStamp();
    }

    public ClubMemberSql(eu.chessdata.backend.tournamentEndpoint.model.ClubMember clubMember){
        this.clubMemberId = clubMember.getClubMemberId();
        this.profileId = clubMember.getProfileId();
        this.clubId = clubMember.getClubId();
        this.guestProfile = clubMember.getGuestProfile();
        this.managerProfile = clubMember.getManagerProfile();
        this.archived = clubMember.getArchived();
        this.dateCreated = clubMember.getDateCreated();
        this.updateStamp = clubMember.getUpdateStamp();
    }

    public ClubMemberSql(eu.chessdata.backend.profileEndpoint.model.ClubMember clubMember){
        this.clubMemberId = clubMember.getClubMemberId();
        this.profileId = clubMember.getProfileId();
        this.clubId = clubMember.getClubId();
        this.guestProfile = clubMember.getGuestProfile();
        this.managerProfile = clubMember.getManagerProfile();
        this.archived = clubMember.getArchived();
        this.dateCreated = clubMember.getDateCreated();
        this.updateStamp = clubMember.getUpdateStamp();
    }

    //boolean default generation problems
    public boolean getArchived(){
        return isArchived();
    }

    public boolean getGuestProfile(){
        return isGuestProfile();
    }

    public boolean getManagerProfile(){
        return isManagerProfile();
    }

    //getters and setters


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
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
