package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.Date;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.entities.ClubManager;
import eu.chessdata.backend.entities.Tournament;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;

import static eu.chessdata.backend.tools.OfyService.factory;
import static eu.chessdata.backend.tools.OfyService.ofy;

/**
 * Created by Bogdan Oloeriu on 23/01/2016.
 */

@Api(
        name="tournamentEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.chessdata.eu",
                ownerName = "backend.chessdata.eu",
                packagePath = ""
        )
)
public class TournamentEndpoint {

    public Tournament generateIndexes(){
        Tournament indexTournament = new Tournament();
        String profileIdExample = "107630334260856779717";
        Long clubIdExample = 7040001L;

        //==========================
        Query<ClubManager> q = ofy().load().type(ClubManager.class);
        q = q.filter("profileId =", profileIdExample);
        q = q.filter("clubId = ", clubIdExample);
        int i=0;
        for (ClubManager clubManager : q){
            i++;
        }
        if (i== 0) {
            final Key<ClubManager> managerKey = factory().allocateId(ClubManager.class);
            ClubManager clubManager = new ClubManager(managerKey.getId(), profileIdExample,
                    clubIdExample, 1L);
            ofy().save().entities(clubManager).now();
        }
        //==========================

        boolean isClubManager = MySecurityService.isClubManager(profileIdExample, clubIdExample);
        indexTournament.setDescription("Ok indexes should be generated: "
                + isClubManager);
        return indexTournament;
    }

    @ApiMethod(httpMethod = "post")
    public Tournament create(Tournament tournament, @Named("idTokenString") String idTokenString){
        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        Tournament ilegalTournament = new Tournament();
        if (secPair.getKey() != MySecurityService.Status.VALID_USER){
            System.out.println("Illegal request " + idTokenString);
            ilegalTournament.setDescription("Illegal idTokenString: " + idTokenString);
            return ilegalTournament;
        }
        String profileId =((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, tournament.getClubId())){
            ilegalTournament.setDescription("Illegal not a club manager");
            return ilegalTournament;
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
        return tournament;
    }
}
