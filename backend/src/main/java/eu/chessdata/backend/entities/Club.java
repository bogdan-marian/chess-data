package eu.chessdata.backend.entities;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Link;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

/**
 * Created by bogdan on 20/12/2015.
 */
@Entity
public class Club {
    @Id
    private Long clubId;
    private String name;
    private String shortName;
    private Email email;
    private String country;
    private String city;
    private Link homePage;
    private String description;
    private long dateCreated;
    private long updateStamp;

    //constructors
    public Club(){}
//    public Club (String name,
//                 String shortName,
//                 Email email,
//                 String country,
//                 String city,
//                 Link homePage,
//                 String description){
//        this.name = name;
//        this.shortName = shortName;
//        this.email = email;
//        this.country = country;
//        this.city = city;
//        this.homePage = homePage;
//        this.description = description;
//
//        long date = new Date().getTime();
//        this.dateCreated = date;
//        this.updateStamp = date;
//    }

    //generated
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Link getHomePage() {
        return homePage;
    }

    public void setHomePage(Link homePage) {
        this.homePage = homePage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
