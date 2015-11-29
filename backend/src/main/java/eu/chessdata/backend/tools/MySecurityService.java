package eu.chessdata.backend.tools;

import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import com.google.appengine.repackaged.com.google.common.base.Pair;


/**
 * Created by bogda on 26/11/2015.
 */
public class MySecurityService {
    public static enum Status{
        VALID_USER,
        INVALID_USER
    }

    public static String getGoogleObjectId(String idTokenString){
        try {
            GoogleIdTokenVerifier verifier = MyGlobalSharedObjects.getGoogleIdTokenVerifier();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null){
                Payload payload = idToken.getPayload();
                String userId = payload.getSubject();
                String email = payload.getEmail();
                String returnValue = email+"("+userId+")";
                return returnValue;
            }
        } catch (GeneralSecurityException e) {
            return ("null");
        } catch (IOException e) {
            return("null");
        }
        return "null";
    }

    public static MyEntry<Status,Payload> getProfile(String idTokenString){

        try {
            GoogleIdTokenVerifier verifier = MyGlobalSharedObjects.getGoogleIdTokenVerifier();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null){
                //new Pair<Status,Payload>();
                MyEntry<Status,Payload> entry = new MyEntry<>(Status.VALID_USER,idToken.getPayload());
                return entry;
            }else {
                return new MyEntry<>(Status.INVALID_USER, new Payload());
            }
        } catch (GeneralSecurityException e) {
            return new MyEntry<>(Status.INVALID_USER, new Payload());
        } catch (IOException e) {
            return new MyEntry<>(Status.INVALID_USER, new Payload());
        }
    }
}
