package eu.chessdata.data;

import android.content.ContentValues;

import java.util.Date;

import eu.chessdata.data.ChessDataContract.ProfileEntry;

/**
 * Created by bogda on 29/11/2015.
 */
public class TestUtilities {
    static ContentValues createProfileValues (String profileId){
        ContentValues profileValues = new ContentValues();
        profileValues.put(ProfileEntry.COLUMN_PROFILE_ID,profileId);
        profileValues.put(ProfileEntry.COLUMN_EMAIL,"testmail@mail.com");
        profileValues.put(ProfileEntry.COLUMN_NAME,"Test Name");
        Date dateValue = new Date();
        String date = dateValue.toString();
        profileValues.put(ProfileEntry.COLUMN_DATE_DATE_OF_BIRTH,date);
        profileValues.put(ProfileEntry.COLUMN_ELO,0);
        profileValues.put(ProfileEntry.COLUMN_ALT_ELO,0);
        profileValues.put(ProfileEntry.COLUMN_DATE_CREATED,date);
        profileValues.put(ProfileEntry.COLUMN_UPATE_STAMP,date);
        return profileValues;
    }
}
