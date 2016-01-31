package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.Key;

import java.util.Date;

import eu.chessdata.backend.entities.ClubMember;
import eu.chessdata.backend.entities.Profile;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;

import static eu.chessdata.backend.tools.OfyService.factory;
import static eu.chessdata.backend.tools.OfyService.ofy;

/**
 * Created by Bogdan Oloeriu on 27/01/2016.
 */
@Api(
        name = "virtualProfileEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.chessdata.eu",
                ownerName = "backend.chessdata.eu",
                packagePath = ""
        )
)
public class VirtualProfileEndpoint {

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
}
