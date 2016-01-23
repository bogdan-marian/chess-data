package eu.chessdata.backend.entities;

import com.google.appengine.api.datastore.Email;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

/**
 * Created by Bogdan on 23/01/2016.
 */
@Entity
public class Tournament {
    @Id
    private Long tournamentId;
    private Long clubId;
    private String name;
    private String description;
    private int totalRounds;
    private long startDate;
    private long endDate;
    private String location;
    private long dateCreated;
    private long updateStamp;

    //constructors
    public Tournament(){}
    public Tournament(String name,
                      String description,
                      int totalRounds,
                      long startDate,
                      long endDate,
                      String location){
        long creationTime = new Date().getTime();

        this.name = name;
        this.description = description;
        this.totalRounds = totalRounds;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.dateCreated = creationTime;
        this.updateStamp = creationTime;
    }
    //generated

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
