package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.entities.Tournament;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;

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
    public Tournament create(Tournament tournament, @Named("idTokenString") String idTokenString){
        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() != MySecurityService.Status.VALID_USER){
            System.out.println("Illegal request " + idTokenString);
            Tournament ilegalTournament = new Tournament();
            ilegalTournament.setDescription("Illegal idTokenString: " + idTokenString);
            return ilegalTournament;
        }
        String profileId =((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        MySecurityService.canCreateTournament(profileId, tournament);

        Tournament vipTournament = new Tournament();
        vipTournament.setName("Some name");
        vipTournament.setDescription(idTokenString);
        return vipTournament;
    }
}
