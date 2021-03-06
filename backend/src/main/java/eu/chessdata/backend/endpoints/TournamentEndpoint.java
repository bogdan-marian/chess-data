package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.SimpleQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.entities.ClubMember;
import eu.chessdata.backend.entities.Game;
import eu.chessdata.backend.entities.Profile;
import eu.chessdata.backend.entities.Round;
import eu.chessdata.backend.entities.RoundPlayer;
import eu.chessdata.backend.entities.SupportObject;
import eu.chessdata.backend.entities.Tournament;
import eu.chessdata.backend.entities.TournamentPlayer;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;

import static eu.chessdata.backend.tools.OfyService.factory;
import static eu.chessdata.backend.tools.OfyService.ofy;

/**
 * Created by Bogdan Oloeriu on 23/01/2016.
 */

@Api(
        name = "tournamentEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.chessdata.eu",
                ownerName = "backend.chessdata.eu",
                packagePath = ""
        )
)
public class TournamentEndpoint {
    private static final Logger log = Logger.getLogger("chess-data");

    @ApiMethod(name = "debugRound", httpMethod = "post")
    public Round debugRound() {
        final Key<Round> key = factory().allocateId(Round.class);
        Long roundId = key.getId();
        Round round = new Round(key.getId(), 100l, 1, false, 1001l);
        ofy().save().entity(round).now();
        return round;
    }

    @ApiMethod(name = "create", httpMethod = "post")
    public Tournament create(Tournament tournament, @Named("idTokenString") String idTokenString) {
        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        Tournament illegalTournament = new Tournament();
        if (secPair.getKey() != MySecurityService.Status.VALID_USER) {
            illegalTournament.setDescription("Not created: Illegal idTokenString: " + idTokenString);
            return illegalTournament;
        }
        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, tournament.getClubId())) {
            illegalTournament.setDescription("Not created: Illegal not a club manager");
            return illegalTournament;
        }
        final Key<Tournament> tournamentKey = factory().allocateId(Tournament.class);
        tournament.setTournamentId(tournamentKey.getId());
        Long time = (new Date()).getTime();
        tournament.setStartDate(time);
        tournament.setEndDate(time);
        tournament.setDateCreated(time);
        tournament.setUpdateStamp(time);
        //store the entity in datastore
        ofy().save().entity(tournament).now();

        //for each tournament round create a round
        Long tournamentId = tournamentKey.getId();
        for (int roundNumber = 1; roundNumber <= tournament.getTotalRounds(); roundNumber++) {
            final Key<Round> key = factory().allocateId(Round.class);
            Long roundId = key.getId();
            Round round = new Round(roundId, tournamentId, roundNumber, false, time);
            //store the round in datastore
            ofy().save().entity(round).now();
        }

        return tournament;
    }

    @ApiMethod(name = "tournamentAddPlayer", httpMethod = "post")
    public TournamentPlayer tournamentAddPlayer(TournamentPlayer tournamentPlayer,
                                                @Named("idTokenString") String idTokenString) {

        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        TournamentPlayer illegalPlayer = new TournamentPlayer();
        if (secPair.getKey() != MySecurityService.Status.VALID_USER) {
            illegalPlayer.setProfileId("Not created: Illegal idTokenString: " + idTokenString);
            return illegalPlayer;
        }
        //find the tournament
        Tournament tournament = ofy().load().type(Tournament.class).id(tournamentPlayer.getTournamentId()).now();
        if (tournament == null) {
            illegalPlayer.setProfileId("Not created: Not able to locate tournament: " + tournamentPlayer.getTournamentId());
            return illegalPlayer;
        }

        //check if current user is a club manager
        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, tournament.getClubId())) {
            illegalPlayer.setProfileId("Not created: Illegal request not a club manager");
            return illegalPlayer;
        }

        //check if new player is not already playing in the tournament
        Query<TournamentPlayer> q = ofy().load().type(TournamentPlayer.class);
        q = q.filter("tournamentId =", tournamentPlayer.getTournamentId());
        q = q.filter("profileId =", tournamentPlayer.getProfileId());
        for (TournamentPlayer player : q) {
            return player;
        }

        //new tournament player so wee persist the data
        final Key<TournamentPlayer> tournamentPlayerKey = factory().allocateId(TournamentPlayer.class);
        tournamentPlayer.setTournamentPlayerId(tournamentPlayerKey.getId());
        Long time = (new Date()).getTime();
        tournamentPlayer.setDateCreated(time);
        tournamentPlayer.setUpdateStamp(time);
        ofy().save().entity(tournamentPlayer).now();

        return tournamentPlayer;
    }

    @ApiMethod(name = "getAllClubsUserIsMember", httpMethod = "post")
    public List<Club> getAllClubsUserIsMember(@Named("idTokenString") String idTokenString) {
        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        List<Club> illegalList = new ArrayList<>();
        Club illegalClub = new Club();
        illegalList.add(illegalClub);
        if (secPair.getKey() != MySecurityService.Status.VALID_USER) {
            illegalClub.setName("Something is wrong: Illegal idTokenString: " + idTokenString);
            return illegalList;
        }


        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        //find the id list
        List<Long> clubIds = new ArrayList<>();
        List<Key<Club>> clubKeys = new ArrayList<>();
        Query<ClubMember> q = ofy().load().type(ClubMember.class);
        q = q.filter("profileId =", profileId);

        for (ClubMember member : q) {
            long clubId = member.getClubId();
            Key<Club> clubKey = Key.create(Club.class, clubId);
            clubKeys.add(clubKey);
            clubIds.add(member.getClubId());
        }


        //find the club list
        SimpleQuery<Club> clubQuery = ofy().load().type(Club.class).filterKey("in", clubKeys);
        List<Club> clubList = new ArrayList<>();
        for (Club club : clubQuery) {
            clubList.add(club);
        }
        return clubList;
    }

    /**
     * Builds and returns a list of ClubMembers
     *
     * @param supportObject contains a list of club ids for witch the members will be loaded
     * @return List of club members
     */
    @ApiMethod(name = "getAllMembers", httpMethod = "post")
    public List<ClubMember> getAllMembers(SupportObject supportObject) {
        List<Long> clubIds = supportObject.getLongList();
        List<ClubMember> illegalList = new ArrayList<>();
        ClubMember illegalMember = new ClubMember();
        illegalList.add(illegalMember);
        if (clubIds.size() == 0) {
            return new ArrayList<>();
        }

        //find the members
        SimpleQuery<ClubMember> memberQuery = ofy().load().type(ClubMember.class).filter("clubId in", clubIds);
        List<ClubMember> members = new ArrayList<>();
        for (ClubMember member : memberQuery) {
            members.add(member);
        }
        return members;
    }

    /**
     * Builds and returns a list of Profiles based on the supplied profileId list
     * contained inside the stringList member of the supportObject
     *
     * @param supportObject contains the list of profile ids
     * @return
     */
    @ApiMethod(name = "getProfileListByProfileIdList", httpMethod = "post")
    public List<Profile> getProfileListByProfileIdList(SupportObject supportObject) {
        List<String> profileIds = supportObject.getStringList();

        if (profileIds.size() == 0) {
            return new ArrayList<>();
        }

        //build the keys
        List<Key<Profile>> profileKeys = new ArrayList<>();
        for (String profileId : profileIds) {
            Key<Profile> profileKey = Key.create(Profile.class, profileId);
            profileKeys.add(profileKey);
        }

        //find the profiles
        List<Profile> profileList = new ArrayList<>();
        SimpleQuery<Profile> profileQuery = ofy().load().type(Profile.class).filterKey("in", profileKeys);
        for (Profile profile : profileQuery) {
            profileList.add(profile);
        }
        return profileList;
    }

    @ApiMethod(name = "getTournamentsByClubIds", httpMethod = "post")
    public List<Tournament> getTournamentsByClubIds(SupportObject supportObject) {
        List<Long> clubIds = supportObject.getLongList();
        if (clubIds.size() == 0) {
            return new ArrayList<>();
        }

        //find the tournaments
        List<Tournament> tournaments = new ArrayList<>();
        SimpleQuery<Tournament> tournamentsQuery = ofy().load().type(Tournament.class).filter("clubId in", clubIds);
        for (Tournament tournament : tournamentsQuery) {
            tournaments.add(tournament);
        }
        return tournaments;
    }

    @ApiMethod(name = "getTournamentPlayersByTournamentIds", httpMethod = "post")
    public List<TournamentPlayer> getTournamentPlayersByTournamentIds(SupportObject supportObject) {
        List<Long> tournamentIds = supportObject.getLongList();
        if (tournamentIds.size() == 0) {
            return new ArrayList<>();
        }

        //find tournamentPlayers
        List<TournamentPlayer> tournamentPlayers = new ArrayList<>();
        SimpleQuery<TournamentPlayer> playersQuery = ofy().load().type(TournamentPlayer.class).filter("tournamentId in", tournamentIds);
        for (TournamentPlayer tournamentPlayer : playersQuery) {
            tournamentPlayers.add(tournamentPlayer);
        }
        return tournamentPlayers;
    }

    @ApiMethod(name = "getRoundsByTournamentIds", httpMethod = "post")
    public List<Round> getRoundsByTournamentIds(SupportObject supportObject) {
        List<Long> tournamentIds = supportObject.getLongList();
        if (tournamentIds.size() == 0) {
            return new ArrayList<>();
        }

        //find rounds
        List<Round> rounds = new ArrayList<>();
        SimpleQuery<Round> roundsQuery = ofy().load().type(Round.class).filter("tournamentId in", tournamentIds);
        for (Round round : roundsQuery) {
            rounds.add(round);
        }
        return rounds;
    }

    @ApiMethod(name = "getRoundPlayersByRoundIds", httpMethod = "post")
    public List<RoundPlayer> getRoundPlayersByRoundIds(SupportObject supportObject) {
        List<Long> roundIds = supportObject.getLongList();
        if (roundIds.size() == 0) {
            return new ArrayList<>();
        }
        List<RoundPlayer> roundPlayers = new ArrayList<>();
        SimpleQuery<RoundPlayer> roundPlayerQuery = ofy().load().type(RoundPlayer.class).filter("roundId in", roundIds);
        for (RoundPlayer roundPlayer : roundPlayerQuery) {
            roundPlayers.add(roundPlayer);
        }
        return roundPlayers;
    }

    @ApiMethod(name = "getGamesByRoundIds", httpMethod = "post")
    public List<Game> getGamesByRoundIds(SupportObject supportObject){
        List<Long> roundIds = supportObject.getLongList();
        if (roundIds.size() == 0) {
            return new ArrayList<>();
        }
        List<Game> games = new ArrayList<>();
        SimpleQuery<Game> gameSimpleQuery = ofy().load().type(Game.class).filter("roundId in", roundIds);
        for(Game game: gameSimpleQuery){
            games.add(game);
        }
        return games;
    }

    @ApiMethod(name = "roundAddPlayer", httpMethod = "post")
    public RoundPlayer roundAddPlayer(@Named("idTokenString") String idTokenString,
                                      RoundPlayer roundPlayer) {
        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        RoundPlayer illegalPlayer = new RoundPlayer();
        if (secPair.getKey() != MySecurityService.Status.VALID_USER) {
            illegalPlayer.setProfileId("Not created: Illegal idTokenString: " + idTokenString);
            return illegalPlayer;
        }

        //find the round
        Round round = ofy().load().type(Round.class).id(roundPlayer.getRoundId()).now();
        if (round == null) {
            illegalPlayer.setProfileId("Not created: not able to find the round: " + roundPlayer.getRoundId());
            return illegalPlayer;
        }

        //find the tournament
        Tournament tournament = ofy().load().type(Tournament.class).id(round.getTournamentId()).now();
        if (tournament == null) {
            illegalPlayer.setProfileId("Not created: Not able to locate tournament: " + round.getTournamentId());
            return illegalPlayer;
        }

        //check if current user is a club manager
        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, tournament.getClubId())) {
            illegalPlayer.setProfileId("Not created: Illegal request not a club manager");
            return illegalPlayer;
        }

        //check if new player is not already playing in the round
        Query<RoundPlayer> q = ofy().load().type(RoundPlayer.class);
        q = q.filter("roundId =", roundPlayer.getRoundId());
        q = q.filter("profileId", roundPlayer.getProfileId());
        for (RoundPlayer player : q) {
            return player;
        }

        //new tournament player so wee persist the data
        final Key<RoundPlayer> roundPlayerKey = factory().allocateId(RoundPlayer.class);
        roundPlayer.setRoundPlayerId(roundPlayerKey.getId());
        Long time = (new Date()).getTime();
        roundPlayer.setDateCreated(time);
        roundPlayer.setUpdateStamp(time);
        ofy().save().entity(roundPlayer).now();
        return roundPlayer;
    }

    @ApiMethod(name = "gameCreateGame", httpMethod = "post")
    public Game gameCreateGame(@Named("idTokenString") String idTokenString,
                               Game game) {
        //check if idTokenString exists
        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        Game illegalGame = new Game();
        if (secPair.getKey() != MySecurityService.Status.VALID_USER) {
            illegalGame.setWhitePlayerId("Not created: Illegal idTokenString: " + idTokenString);
            return illegalGame;
        }

        //find the round
        Round round = ofy().load().type(Round.class).id(game.getRoundId()).now();
        if (round == null) {
            illegalGame.setWhitePlayerId("Not created: not able to find the round: " + game.getRoundId());
            return illegalGame;
        }

        //find the tournament
        Tournament tournament = ofy().load().type(Tournament.class).id(round.getTournamentId()).now();
        if (tournament == null) {
            illegalGame.setWhitePlayerId("Not created: Not able to locate tournament: " + round.getTournamentId());
            return illegalGame;
        }

        //check if current user is a club manager
        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, tournament.getClubId())) {
            illegalGame.setWhitePlayerId("Not created: Illegal request not a club manager");
            return illegalGame;
        }

        //check if new game is not already in datastore
        Query<Game> q = ofy().load().type(Game.class);
        q = q.filter("roundId =", game.getRoundId());
        q = q.filter("tableNumber =", game.getTableNumber());
        for (Game foundGame : q) {
            return foundGame;
        }

        //new game so wee persist data
        final Key<Game> gameKey = factory().allocateId(Game.class);
        game.setGameId(gameKey.getId());
        Long time = (new Date()).getTime();
        game.setDateCreated(time);
        game.setUpdateStamp(time);
        ofy().save().entity(game).now();

        return game;
    }

    @ApiMethod(name = "gameUpdate", httpMethod = "post")
    public Game gameUpdate(@Named("idTokenString") String idTokenString, Game game){
        Game illegalGame = new Game();
        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() != MySecurityService.Status.VALID_USER) {
            illegalGame.setWhitePlayerId("No update: Illegal idTokenString: " + idTokenString);
            return illegalGame;
        }

        //find the round
        Round round = ofy().load().type(Round.class).id(game.getRoundId()).now();
        if (round == null) {
            illegalGame.setWhitePlayerId("No update: not able to find the round: " + game.getRoundId());
            return illegalGame;
        }

        //find the tournament
        Tournament tournament = ofy().load().type(Tournament.class).id(round.getTournamentId()).now();
        if (tournament == null) {
            illegalGame.setWhitePlayerId("No update: Not able to locate tournament: " + round.getTournamentId());
            return illegalGame;
        }

        //check if current user is a club manager
        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, tournament.getClubId())) {
            illegalGame.setWhitePlayerId("No update: Illegal request not a club manager");
            return illegalGame;
        }

        //select the game
        Game vipGame = ofy().load().type(Game.class).id(game.getGameId()).now();
        if (vipGame == null){
            illegalGame.setWhitePlayerId("No update: Not able to locate the game: " + game.getGameId());
            return illegalGame;
        }
        //compare updateStamps
        if (game.getUpdateStamp()< vipGame.getUpdateStamp()){
            //no update just return
            return vipGame;
        }else{
            //update dataStore and return
            ofy().save().entity(game).now();
            return  game;
        }
    }
}
