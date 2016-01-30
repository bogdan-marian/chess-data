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
    private long dateOfBirth;
    private int elo;
    private int altElo;
    private boolean virtualProfile;
    private long dateCreated;
    private long updateStamp;

    //constructors;
    public Profile(){}

    /**
     * Used when creating an authenticated user
     * @param profileId
     * @param email
     * @param name
     */
    public Profile(String profileId, Email email, String name){
        this.profileId = profileId;
        this.email=email;
        this.name = name;

        long date = new Date().getTime();
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

    public boolean isVirtualProfile() {
        return virtualProfile;
    }

    public void setVirtualProfile(boolean virtualProfile) {
        this.virtualProfile = virtualProfile;
    }
}
