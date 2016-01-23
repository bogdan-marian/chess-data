package eu.chessdata.backend.tools;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import eu.chessdata.backend.entities.Club;
import eu.chessdata.backend.entities.ClubManager;
import eu.chessdata.backend.entities.ClubMember;
import eu.chessdata.backend.entities.Profile;
import eu.chessdata.backend.entities.Tournament;

/**
 * Created by bogda on 25/11/2015.
 */
public class OfyService {
    static{
        //ObjectifyService.register(Quote.class);
        ObjectifyService.register(Club.class);
        ObjectifyService.register(ClubManager.class);
        ObjectifyService.register(ClubMember.class);
        ObjectifyService.register(Profile.class);
        ObjectifyService.register(Tournament.class);
    }

    public static Objectify ofy(){
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory(){
        return ObjectifyService.factory();
    }
}
