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
     * Create the club and also add the club member and set
     * <p/>
     * managerProfile = true
     * questProfile = false
     * archived = false
     *
     * @param club
     * @param idTokenString
     * @return
     */
    @ApiMethod(name = "create", httpMethod = "post")
    public Club create(Club club, @Named("idTokenString") String idTokenString) {
        MyEntry<Status, GoogleIdToken.Payload> secPair = MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() != Status.VALID_USER) {
            System.out.println("Illegal request " + idTokenString);
            return new Club();
        } else {
            //create the club
            final Key<Club> clubKey = factory().allocateId(Club.class);
            club.setClubId(clubKey.getId());
            Long time = new Date().getTime();

            club.setDateCreated(time);
            club.setUpdateStamp(time);
            ofy().save().entity(club).now();

            //create the clubMember (first manager)
            String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
            final Key<ClubMember> memberKey = factory().allocateId(ClubMember.class);

            ClubMember clubMember = new ClubMember(memberKey.getId(),
                    profileId,
                    clubKey.getId(),
                    false,//quest profile
                    true, //manager profile
                    false, //archived
                    time,
                    time);

            ofy().save().entity(clubMember).now();

            return club;
        }
    }

    /**
     * Access this on the client side immediate after creating the club in
     * order to get also  the clubMember information
     * @param idTokenString
     * @return clubId
     */
    @ApiMethod(name = "getFirstManager", httpMethod = "post")
    public ClubMember getFirstManager(@Named("idTokenString") String idTokenString,
                                      @Named("clubId") Long clubId){
        ClubMember illegalMember = new ClubMember();

        MyEntry<Status, GoogleIdToken.Payload> secPair = MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() != Status.VALID_USER) {
            illegalMember.setProfileId("Illegal request: No valid user");
            return illegalMember;
        }

        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        Query<ClubMember> query = ofy().load().type(ClubMember.class);
        query = query.filter("profileId", profileId);
        query = query.filter("clubId",clubId);
        ClubMember firstManager = query.first().now();
        if (firstManager != null){
            return firstManager;
        }
        illegalMember.setProfileId("Illegal request:I was not able to locate the firstManager");
        return illegalMember;
    }



    @ApiMethod(name = "debugClubMember", httpMethod = "post")
    public ClubMember debugClubMember(ClubMember clubMember) {
        ClubMember vipMember = new ClubMember();
        vipMember.setProfileId("Not created: just a debug message");
        return vipMember;
    }
}
