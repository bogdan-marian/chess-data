package eu.chessdata.backend.tools;

import com.google.appengine.repackaged.com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.appengine.repackaged.com.google.api.client.http.HttpTransport;
import com.google.appengine.repackaged.com.google.api.client.http.javanet.NetHttpTransport;
import com.google.appengine.repackaged.com.google.api.client.json.JsonFactory;
import com.google.appengine.repackaged.com.google.api.client.json.jackson.JacksonFactory;

import java.util.Arrays;

/**
 * Created by bogda on 26/11/2015.
 */
public class MyGlobalSharedObjects {
    private static String[] CLIENT_ID = new String[]{
            "AIzaSyCKMLUTAZ48Z-TQC6sQpyARnQn-5oBpi6g",
            "264714598866-m9n6qopvg0l815u9q4bi9tahhvq3lucu.apps.googleusercontent.com",
            "264714598866-ne3a6fp88topjmnidocrt86u7inokqlr.apps.googleusercontent.com"
    };
    public static final String ROOT_URL = "https://chess-data.appspot.com/_ah/api/";

    private static JsonFactory jsonFactory = new JacksonFactory();

    private static HttpTransport httpTransport = new NetHttpTransport();

    private static GoogleIdTokenVerifier googleIdTokenVerifier =
            new GoogleIdTokenVerifier.Builder(
                    MyGlobalSharedObjects.getHttpTransport(),
                    MyGlobalSharedObjects.getJsonFactory())
                    .setAudience(Arrays.asList(CLIENT_ID))
                    .build();

    public static JsonFactory getJsonFactory() {
        return MyGlobalSharedObjects.jsonFactory;
    }

    public static HttpTransport getHttpTransport() {
        return MyGlobalSharedObjects.httpTransport;
    }

    public static GoogleIdTokenVerifier getGoogleIdTokenVerifier(){
        return MyGlobalSharedObjects.googleIdTokenVerifier;
    }
}
