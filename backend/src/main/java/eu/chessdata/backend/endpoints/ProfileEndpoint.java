package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.appengine.repackaged.com.google.common.base.Pair;
import com.googlecode.objectify.Key;

import eu.chessdata.backend.entities.Profile;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;
import eu.chessdata.backend.tools.MySecurityService.Status;

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
     * //todo: experiment with what to do if request can not validate
     *
     * @return
     */
    @ApiMethod(name = "getProfile", httpMethod = "post")
    public Profile getProfile(@Named("idTokenString") String idTokenString) {
        MyEntry<Status, Payload> secPair = MySecurityService.getProfile(idTokenString);
        if (secPair.getKey() == Status.VALID_USER) {
            //look in datastore and see if wee have this user
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
}
