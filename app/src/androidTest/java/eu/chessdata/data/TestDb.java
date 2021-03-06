package eu.chessdata.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by bogda on 29/11/2015.
 */
public class TestDb extends AndroidTestCase {
    private String TAG = "bogdan";

    void deleteTheDatabase(){
        mContext.deleteDatabase(ChessDataDbHelper.DATABASE_NAME);
    }

    public void setUp(){
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable{
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(ChessDataContract.ProfileEntry.TABLE_NAME);

        mContext.deleteDatabase(ChessDataDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ChessDataDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without the specified entry tables",
                tableNameHashSet.isEmpty());

        db.close();
    }

    public void testProfileTable(){
        Log.d(TAG, "launch insert profile?");
        insertProfile();
    }
    public long insertProfile(){
        ChessDataDbHelper dbHelper = new ChessDataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String testProfile1 = "testProfile1";
        ContentValues testValues = TestUtilities.createProfileValues(testProfile1);
        long profileRowId;
        profileRowId = db.insert(ChessDataContract.ProfileEntry.TABLE_NAME,null,testValues);
        Log.d(TAG,"profileRowId = " + profileRowId);
        assertTrue(profileRowId != -1);
        // data is inserted

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(ChessDataContract.ProfileEntry.TABLE_NAME,
                null,//all columns
                null,//columns for the "where" clause
                null, //values for the "where" clause
                null, //columns to group by
                null, //columns to filter by row groups
                null  //sort order )
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from profile query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Profile Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                cursor.moveToNext() );
        return 1;
    }
}
