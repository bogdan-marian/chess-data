package eu.chessdata.backend.entities;

import com.google.appengine.api.datastore.Email;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Bogdan Oloeriu on 27/01/2016.
 */
@Entity
public class VirtualProfile {
    @Id
    private Long virtualProfileId;
    private Email email;
    private String name;
    private long dateOfBirth;
    private int elo;
    private int altElo;
    private long dateCreated;
    private long updateStamp;

    //constructors
    public VirtualProfile(){}
    public VirtualProfile(Email email,
                          String name,
                          long dateOfBirth,
                          int elo,
                          int altElo,
                          long dateCreated,
                          long updateStamp){
        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.elo = elo;
        this.altElo = altElo;
        this.dateCreated = dateCreated;
        this.updateStamp = updateStamp;
    }

    //generated

    public Long getVirtualProfileId() {
        return virtualProfileId;
    }

    public void setVirtualProfileId(Long virtualProfileId) {
        this.virtualProfileId = virtualProfileId;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getAltElo() {
        return altElo;
    }

    public void setAltElo(int altElo) {
        this.altElo = altElo;
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
