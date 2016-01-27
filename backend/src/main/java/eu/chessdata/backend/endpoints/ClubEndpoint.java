package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.Key;

import java.util.Date;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.entities.ClubManager;
import eu.chessdata.backend.entities.ClubMember;
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
            Date date = new Date();
            club.setDateCreated(date.getTime());
            club.setUpdateStamp(date.getTime());
            ofy().save().entity(club).now();

            //create ClubManager
            String profileId =((GoogleIdToken.Payload) secPair.getValue()).getSubject();
            final Key<ClubManager> managerKey = factory().allocateId(ClubManager.class);
            ClubManager clubManager = new ClubManager(
                    managerKey.getId(), profileId,clubKey.getId(),date.getTime());
            ofy().save().entity(clubManager).now();

            //createClubMember
            final Key<ClubMember> memberKey = factory().allocateId(ClubMember.class);

            ClubMember clubMember = new ClubMember(
                    memberKey.getId(),profileId,null,clubKey.getId(),date.getTime(),date.getTime());
            ofy().save().entity(clubMember).now();

            return  club;
        }
    }
}
