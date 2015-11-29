package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by bogda on 25/11/2015.
 */
@Entity
public class Quote {
    @Id
    private Long quoteId;
    private String who;
    private String what;
    private Long counter = 0L;
    //constructors
    public Quote(){}
    public Quote(Long quoteId,String who, String what){
        this.quoteId = quoteId;
        this.who = who;
        this.what = what;
    }
    //generated items

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }
}
