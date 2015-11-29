package eu.chessdata.data;

import android.provider.BaseColumns;

/**
 * Created by bogda on 29/11/2015.
 */
public class ChessDataContract {
    /*
    Inner class that defines the conent of the location table
     */
    public static final class ProfileEntry implements BaseColumns{
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_PROFILE_ID = "profile_id";
        public static final String COLUMN_EMAIL="email";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE_DATE_OF_BIRTH = "date_of_birth";
        public static final String COLUMN_ELO="elo";
        public static final String COLUMN_ALT_ELO="alt_elo";
        public static final String COLUMN_DATE_CREATED="date_created";
        public static final String COLUMN_UPATE_STAMP="update_stamp";
    }
}
