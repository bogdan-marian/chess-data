package eu.chessdata.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import eu.chessdata.data.ChessDataContract.ProfileEntry;

/**
 * Created by bogda on 03/12/2015.
 */
public class TestProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    public void deleteAllRecordsFromProvider() {
        /*ChessDataDbHelper dbHelper = new ChessDataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(ProfileEntry.TABLE_NAME, null,null);
        db.close();*/

        mContext.getContentResolver().delete(
                ProfileEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                ProfileEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from profile table ", 0,cursor.getCount());
        cursor.close();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ChessDataProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: ChessDataProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + ChessDataContract.CONTENT_AUTHORITY,
                    providerInfo.authority, ChessDataContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: ChessDataProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
        This test uses the database directly to insert a profile and then uses the ContentProvider to
        read out the data.
     */
    public void testBasicProfileQueries() {
        //insert our test record into the database
        ChessDataDbHelper dbHelper = new ChessDataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createProfileVipValues();
        long profileRowId = TestUtilities.insertProfileValues(mContext, testValues);

        //test the basic content provider query
        Cursor profileCursor = mContext.getContentResolver().query(
                ProfileEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        //make sure that we get the correct cursor out of hte database
        TestUtilities.validateCursor("testBasicProfileQueries, profile query", profileCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    profileCursor.getNotificationUri(), ProfileEntry.CONTENT_URI);
        }
    }

    public void testInsertReadProfile() {
        ContentValues testValues = TestUtilities.createProfileVipValues();

        //register a content observer for our insert.
        //This tome, directly with
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ProfileEntry.CONTENT_URI, true, tco);
        Uri profileUri = mContext.getContentResolver().insert(ProfileEntry.CONTENT_URI, testValues);

        //Did our content resolver get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long profileRowId = ContentUris.parseId(profileUri);

        //Verify wee got a row back.
        assertTrue(profileRowId != -1);

        //
        Cursor cursor = mContext.getContentResolver().query(
                ProfileEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider, Error validating profileEntry. ",
                cursor, testValues);
    }

    public void testDeleteAllRecords() {
        testInsertReadProfile();

        TestUtilities.TestContentObserver profileObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ProfileEntry.CONTENT_URI, true, profileObserver);

        deleteAllRecordsFromProvider();

        //make sure that you are calling
        //getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider delete.
        profileObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(profileObserver);
    }
}
