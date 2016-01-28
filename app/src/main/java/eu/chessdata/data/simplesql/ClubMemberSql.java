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

    @SimpleSQLColumn("virtualProfileId")
    private Long virtualProfileId;

    @SimpleSQLColumn("clubId")
    private Long clubId;

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
        this.virtualProfileId = clubMember.getVirtualProfileId();
        this.clubId = clubMember.getClubId();
        this.archived = clubMember.getArchived();
        this.dateCreated = clubMember.getDateCreated();
        this.updateStamp = clubMember.getUpdateStamp();
    }

    //boolean default generation problems
    public boolean getArchived(){
        return isArchived();
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

    public Long getVirtualProfileId() {
        return virtualProfileId;
    }

    public void setVirtualProfileId(Long virtualProfileId) {
        this.virtualProfileId = virtualProfileId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
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
