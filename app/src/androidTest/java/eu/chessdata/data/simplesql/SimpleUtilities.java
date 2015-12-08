package eu.chessdata.data.simplesql;

import android.test.AndroidTestCase;

import java.util.Date;

/**
 * Created by bogda on 07/12/2015.
 */
public class SimpleUtilities extends AndroidTestCase{
    static Profile createProfileVipValues(){
        Profile vip = new Profile();
        vip.profileId = "profileVip1";
        vip.email = "testmail@mail.com";
        vip.name = "Test Profile Name";
        Date dateValue = new Date();
        long date = dateValue.getTime();
        vip.dateOfBirth = date;
        vip.elo = 0;
        vip.altElo = 0;
        vip.dateCreated = date;
        vip.updateStamp = date;
        return vip;
    }

    static void compareProfilesNoId(Profile profileA, Profile profileB){
        assertTrue("Profile not the same ", profileA.profileId.equals(profileB.profileId));
        assertTrue("email not the same ", profileA.email.equals(profileB.email));
        assertTrue("name not the same ", profileA.name.equals(profileB.name));
        assertTrue("dateOfBirth not the same", profileA.dateOfBirth == profileB.dateOfBirth);
        assertTrue("elo not the same ", profileA.elo == profileB.elo);
        assertTrue("dateCreate not the same ", profileA.dateCreated == profileB.dateCreated);
        assertTrue("updateStamp not the same ", profileA.updateStamp == profileB.updateStamp);
    }
}
