package eu.chessdata.backend.entities;

import com.google.appengine.api.datastore.Email;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

/**
 * Created by bogda on 27/11/2015.
 */
@Entity
public class Profile {
    @Id
    private String profileId;
    private Email email;
    private String name;
    private Date dateOfBirth;
    private int elo;
    private int altElo;
    private Date dateCreated;
    private Date updateStamp;

    //constructors;
    public Profile(){}
    public Profile(String profileId, Email email, String name){
        this.profileId = profileId;
        this.email=email;
        this.name = name;
        Date date = new Date();
        this.dateCreated = date;
        this.updateStamp = date;
    }

    //generated
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(Date updateStamp) {
        this.updateStamp = updateStamp;
    }
}
