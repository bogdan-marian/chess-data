package eu.chessdata.data.simplesql;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;
import eu.chessdata.backend.tournamentEndpoint.model.Round;

/**
 * Created by Bogdan Oloeriu on 28/03/2016.
 */
@SimpleSQLTable(table = "Round", provider = "SimpleProvider")
public class RoundSql {
    @SimpleSQLColumn(value = "_id", primary = true)
    private long id;

    @SimpleSQLColumn(value = "roundId")
    private Long roundId;

    @SimpleSQLColumn(value = "tournamentId")
    private Long tournamentId;

    @SimpleSQLColumn(value = "roundNumber")
    private int roundNumber;

    @SimpleSQLColumn(value = "isLocked")
    private boolean isLocked;

    @SimpleSQLColumn(value = "updateStamp")
    private long updateStamp;

    //constructors
    public RoundSql() {
    }

    public RoundSql(Round round) {
        this.roundId = round.getRoundId();
        this.tournamentId = round.getTournamentId();
        this.roundNumber = round.getRoundNumber();
        this.isLocked = round.getLocked();
        this.updateStamp = round.getUpdateStamp();
    }

    public boolean getIsLocked(){
        return isLocked;
    }

    //getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
