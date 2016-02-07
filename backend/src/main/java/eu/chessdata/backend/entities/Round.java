package eu.chessdata.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Bogdan Oloeriu on 07/02/2016.
 */
@Entity
public class Round {
    @Id
    private Long roundId;
    @Index
    private Long tournamentId;
    @Index
    private int roundNumber;
    private boolean isLocked;
    private long updateStamp;

    //constructors
    public Round(){}
    public Round(Long roundId,
                 Long tournamentId,
                 int roundNumber,
                 boolean isLocked,
                 long updateStamp){
        this.roundId = roundId;
        this.tournamentId = tournamentId;
        this.roundNumber = roundNumber;
        this.isLocked = isLocked;
        this.updateStamp = updateStamp;
    }

    //generated

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public long getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(long updateStamp) {
        this.updateStamp = updateStamp;
    }
}
