package eu.chessdata.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * each table as 2 id columns
 * _id is the inner sqlite id column auto incremented by sqlite
 * tableId is the datastore id.
 * To identify if a entity was not yet created on the datastore is enough to
 * check if tableId column is null.
 */
public class ChessDataContract {
    public static final String CONTENT_AUTHORITY = "eu.chessdata";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_PROFILE = "profile";

    /*
    Inner class that defines the content of the profile table.
     */
    public static final class ProfileEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROFILE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PROFILE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PROFILE;


        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PROFILE_ID ="profileId";
        public static final String COLUMN_EMAIL="email";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE_DATE_OF_BIRTH = "date_of_birth";
        public static final String COLUMN_ELO="elo";
        public static final String COLUMN_ALT_ELO="alt_elo";
        public static final String COLUMN_DATE_CREATED="date_created";
        public static final String COLUMN_UPATE_STAMP="update_stamp";
    }
}
