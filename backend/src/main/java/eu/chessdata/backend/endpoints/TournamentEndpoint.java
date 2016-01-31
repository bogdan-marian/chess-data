package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.Key;

import java.util.Date;

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

    @ApiMethod(name = "create", httpMethod = "post")
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
            System.out.println("ProfileId: " + profileId+ " / clubId: " + tournament.getClubId());
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
