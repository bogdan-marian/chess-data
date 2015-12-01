package eu.chessdata.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.chessdata.data.ChessDataContract.ProfileEntry;

/**
 * Created by bogda on 29/11/2015.
 */
public class ChessDataDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "chess_data.db";

    public ChessDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME +" ("+
                ProfileEntry.COLUMN_ID +" INTEGER PRIMARY KEY, " +
                ProfileEntry.COLUMN_PROFILE_ID+" TEXT UNIQUE, "+
                ProfileEntry.COLUMN_EMAIL+" TEXT UNIQUE NOT NULL, " +
                ProfileEntry.COLUMN_NAME+" TEXT NOT NULL, "+
                ProfileEntry.COLUMN_DATE_DATE_OF_BIRTH +" TEXT NOT NULL, "+
                ProfileEntry.COLUMN_ELO +" INTEGER NOT NULL, "+
                ProfileEntry.COLUMN_ALT_ELO +" INTEGER NOT NULL, "+
                ProfileEntry.COLUMN_DATE_CREATED+" TEXT NOT NULL, "+
                ProfileEntry.COLUMN_UPATE_STAMP+" TEXT NOT NULL"+
                " );";
        db.execSQL(SQL_CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME);
    }
}
