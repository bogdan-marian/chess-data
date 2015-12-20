package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.Key;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;
import eu.chessdata.backend.tools.MySecurityService.Status;

import static eu.chessdata.backend.tools.OfyService.factory;
import static eu.chessdata.backend.tools.OfyService.ofy;

/**
 * Created by bogda on 20/12/2015.
 */

@Api(
        name = "clubEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.chessdata.eu",
                ownerName = "backend.chessdata.eu",
                packagePath = ""
        )
)
public class ClubEndpoint {

    /**
     * Create the club and also add the default club manager and club member
     * TODO add the default club manager and club member
     * @param club
     * @param idTokenString
     * @return
     */
    @ApiMethod(name = "create", httpMethod = "post")
    public Club create(Club club, @Named("idTokenString") String idTokenString){
        MyEntry<Status, GoogleIdToken.Payload> secPair = MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() != Status.VALID_USER){
            System.out.println("Illegal request " + idTokenString);
            return new Club();
        }
        else {
            final Key<Club> clubKey = factory().allocateId(Club.class);
            club.setClubId(clubKey.getId());
            ofy().save().entity(club).now();

            String profileId =((GoogleIdToken.Payload) secPair.getValue()).getSubject();
            System.out.println("time to create manager and member for pair: " +profileId + "/" + clubKey.getId());
            return  club;
        }
    }
}
