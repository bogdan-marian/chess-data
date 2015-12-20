package eu.chessdata.backend.tools;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.entities.Profile;

/**
 * Created by bogda on 25/11/2015.
 */
public class OfyService {
    static{
        //ObjectifyService.register(Quote.class);
        ObjectifyService.register(Profile.class);
        ObjectifyService.register(Club.class);
    }

    public static Objectify ofy(){
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory(){
        return ObjectifyService.factory();
    }
}
