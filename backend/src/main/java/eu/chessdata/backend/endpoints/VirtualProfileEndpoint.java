package eu.chessdata.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import eu.chessdata.backend.entities.VirtualProfile;
import eu.chessdata.backend.tools.MyEntry;
import eu.chessdata.backend.tools.MySecurityService;

/**
 * Created by Bogdan Oloeriu on 27/01/2016.
 */
@Api(
    name="virtualProfileEndpoint",
    version = "v1",
    namespace = @ApiNamespace(
        ownerDomain = "backend.chessdata.eu",
        ownerName = "backend.chessdata.eu",
        packagePath = ""
    )
)
public class VirtualProfileEndpoint {

    @ApiMethod(name = "create", httpMethod = "post")
    public VirtualProfile create(VirtualProfile virtualProfile,
                                 @Named("clubId") Long clubId,
                                 @Named("idTokenString") String idTokenString){
        VirtualProfile vipProfile = new VirtualProfile();
        vipProfile.setName("Not created: Please implement this");

        MyEntry<MySecurityService.Status, GoogleIdToken.Payload> secPair =
                MySecurityService.getProfile(idTokenString);

        if (secPair.getKey() != MySecurityService.Status.VALID_USER){
            vipProfile.setName("Not created: Illegal request idTokenString");
            return vipProfile;
        }

        String profileId =((GoogleIdToken.Payload) secPair.getValue()).getSubject();
        if (!MySecurityService.isClubManager(profileId, clubId)){
            vipProfile.setName("Not created: Illegal request idTokenString");
            return vipProfile;
        }

        return vipProfile;
    }
}
