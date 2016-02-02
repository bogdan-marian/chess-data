package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.util.Date;

import eu.chessdata.backend.entities.ClubMember;
import eu.chessdata.backend.entities.Profile;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;
import eu.chessdata.backend.tools.MySecurityService.Status;

import static eu.chessdata.backend.tools.OfyService.factory;
import static eu.chessdata.backend.tools.OfyService.ofy;

/**
 * Created by bogdan on 28/11/2015.
 */
@Api(
        name = "profileEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.chessdata.eu",
                ownerName = "backend.chessdata.eu",
                packagePath = ""
        )
)
public class ProfileEndpoint {

    /**
     * Authenticate the request store the user in datastore
     * and then return the user. If request not valid
     * todo: experiment with what to do if request can not validate
     *
     * @return
     */
    @ApiMethod(name = "getProfile", httpMethod = "post")
    public Profile getProfile(@Named("idTokenString") String idTokenString) {
        MyEntry<Status, Payload> secPair = MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() == Status.VALID_USER) {
            //look in datastore and see if wee have this user
            System.out.println("Valid idToken = " + idTokenString);
            Payload payload = (Payload) secPair.getValue();
            String profileId = payload.getSubject();
            Key key = Key.create(Profile.class, profileId);
            Profile profile = (Profile) ofy().load().key(key).now();
            if (profile != null) {
                //profile in datastore
                return profile;
            } else {
                //profile not in datastore
                profile = new Profile(profileId,
                        new Email(payload.getEmail()),
                        payload.getEmail());
                ofy().save().entity(profile).now();
                return profile;
            }
        }
        else {
            //user not valid
            System.out.println("Illegal request "+idTokenString);
            return new Profile();
        }
    }

    @ApiMethod(name = "createVirtualProfile", httpMethod = "post")
    public Profile createVirtualProfile(
            @Named("clubId") Long clubId,
            @Named("idTokenString") String idTokenString,
            Profile virtualProfile) {
        Profile illegalProfile = new Profile();
        illegalProfile.setName("Not created: Please implement this");

        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);

        if (secPair.getKey() != MySecurityService.Status.VALID_USER) {
            illegalProfile.setName("Not created: Illegal request idTokenString");
            return illegalProfile;
        }

        String profileId = ((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, clubId)) {
            illegalProfile.setName("Not created: Illegal request not a club manager");
            return illegalProfile;
        }
        //illegalProfile.setName("Not created: security ok so please continue working on it");

        //create the virtualProfile
        final Key<Profile> virtualProfileKey = factory().allocateId(Profile.class);
        virtualProfile.setProfileId(virtualProfileKey.getString());
        Long time = (new Date()).getTime();
        virtualProfile.setDateCreated(time);
        virtualProfile.setUpdateStamp(time);
        ofy().save().entity(virtualProfile).now();

        //create club member
        final Key<ClubMember> memberKey = factory().allocateId(ClubMember.class);

        ClubMember clubMember = new ClubMember(memberKey.getId(),
                virtualProfileKey.getString(),
                clubId,
                false,//guestProfile
                false,// manager profile
                false, // archived
                time,
                time );
        ofy().save().entity(clubMember).now();

        return virtualProfile;
    }

    /**
     * use theas only immediately after you created a new virutal member
     * to get it's synchronization id for clubMember entity
     * @param idTokenString
     * @param profileId
     * @param clubId
     * @return
     */
    public ClubMember getJustCreatedVirtualMember(@Named("idTokenString") String idTokenString,
                                                  @Named("profileId") String profileId,
                                                  @Named("clubId") Long clubId){

        ClubMember illegalMember = new ClubMember();
        MyEntry<Status, GoogleIdToken.Payload> secPair = MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() != Status.VALID_USER) {
            illegalMember.setProfileId("Illegal request: No valid user");
            return illegalMember;
        }

        Query<ClubMember> query = ofy().load().type(ClubMember.class);
        query = query.filter("profileId", profileId);
        query = query.filter("clubId",clubId);
        ClubMember virtualMember = query.first().now();
        if (virtualMember == null){
            illegalMember.setProfileId("Illegal request: No able to locate the member info");
            return illegalMember;
        }
        return virtualMember;
    }
}
