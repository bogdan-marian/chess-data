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
}
