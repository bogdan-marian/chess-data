package eu.chessdata.data.simplesql;

import java.util.Date;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;
import eu.chessdata.backend.tournamentEndpoint.model.Game;

/**
 * Created by Bogdan Oloeriu on 28/03/2016.
 */
@SimpleSQLTable(table="game",provider = "SimpleProvider")
public class GameSql {
    @SimpleSQLColumn(value="_id", primary = true)
    public long id;

    @SimpleSQLColumn(value = "gameId")
    public Long gameId;

    @SimpleSQLColumn(value = "roundId")
    public Long roundId;

    @SimpleSQLColumn(value = "tableNumber")
    private int tableNumber;

    @SimpleSQLColumn(value = "whitePlayerId")
    private String whitePlayerId;

    @SimpleSQLColumn(value = "blackPlayerId")
    private String blackPlayerId;

    @SimpleSQLColumn(value = "result")
    private int result;

    @SimpleSQLColumn(value = "dateCreated")
    private long dateCreated;

    @SimpleSQLColumn(value = "updateStamp")
    private long updateStamp;

    //constructors

    public GameSql() {
    }

    public GameSql(Long roundId,
                   int tableNumber,
                   String whitePlayerId,
                   String blackPlayerId,
                   int result){
        this.roundId = roundId;
        this.tableNumber = tableNumber;
        this.whitePlayerId = whitePlayerId;
        this.blackPlayerId = blackPlayerId;
        this.result = result;
        long date = new Date().getTime();
        this.dateCreated = date;
        this.updateStamp = date;
    }

    public GameSql(Game game) {
        this.gameId = game.getGameId();
        this.roundId = game.getRoundId();
        this.tableNumber = game.getTableNumber();
        this.whitePlayerId = game.getWhitePlayerId();
        this.blackPlayerId = game.getBlackPlayerId();
        this.result = game.getResult();
        this.dateCreated = game.getDateCreated();
        this.updateStamp = game.getUpdateStamp();
    }


    //getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getWhitePlayerId() {
        return whitePlayerId;
    }

    public void setWhitePlayerId(String whitePlayerId) {
        this.whitePlayerId = whitePlayerId;
    }

    public String getBlackPlayerId() {
        return blackPlayerId;
    }

    public void setBlackPlayerId(String blackPlayerId) {
        this.blackPlayerId = blackPlayerId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
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
