package eu.chessdata.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by bogda on 03/12/2015.
 */
public class ChessDataProvider extends ContentProvider{
    //the URI Matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ChessDataDbHelper mOpenHelper;

    static final int PROFILE = 100;

    private static final SQLiteQueryBuilder sProfileQueryBuilder;

    static{
        sProfileQueryBuilder = new SQLiteQueryBuilder();
        sProfileQueryBuilder.setTables(
                ChessDataContract.ProfileEntry.TABLE_NAME
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ChessDataDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case PROFILE:
            {
                retCursor = null;
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return  retCursor;
    }

    @Override
    public String getType(Uri uri) {
        //use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match){
            case PROFILE:
                return ChessDataContract.ProfileEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case PROFILE:{
                long _id = db.insert(ChessDataContract.ProfileEntry.TABLE_NAME,null,values);
                if (_id > 0)
                    returnUri = ChessDataContract.ProfileEntry.buildProfileUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new IllegalStateException("Please finish this");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new IllegalStateException("Please finish this");
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ChessDataContract.CONTENT_AUTHORITY;

        //for each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority,ChessDataContract.PATH_PROFILE, PROFILE);

        return matcher;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PROFILE:
                throw new IllegalStateException("You should never call bulkInsert on "+PROFILE);
            default:
                return super.bulkInsert(uri,values);
        }
    }
}
