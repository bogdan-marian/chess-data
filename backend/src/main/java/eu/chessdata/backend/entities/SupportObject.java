package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;

import java.util.List;

/**
 * Created by Bogdan Oloeriu on 22/03/2016.
 */
@Entity
public class SupportObject {
    private String message;
    private List<Long> longList;
    private List<String> stringList;

    //getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getLongList() {
        return longList;
    }

    public void setLongList(List<Long> longList) {
        this.longList = longList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
