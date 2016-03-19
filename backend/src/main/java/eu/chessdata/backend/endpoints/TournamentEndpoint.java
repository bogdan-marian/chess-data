package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.SimpleQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.entities.ClubMember;
import eu.chessdata.backend.entities.Round;
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

    @ApiMethod(name = "getAllMembers", httpMethod = "post")
    public List<ClubMember> getAllMembers( @Named("clubIds") List<Long> clubIds) {
        List<ClubMember> illegalList = new ArrayList<>();
        ClubMember illegalMember = new ClubMember();
        illegalList.add(illegalMember);
        if (clubIds.size()==0){
            illegalMember.setProfileId("Something is wrong: supplied list was empty");
            return illegalList;
        }
        illegalMember.setProfileId("Something is wrong: list is not empty");
        return illegalList;
    }
}
