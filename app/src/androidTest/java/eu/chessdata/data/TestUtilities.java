package eu.chessdata.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import eu.chessdata.data.ChessDataContract.ProfileEntry;
import eu.chessdata.utils.PollingCheck;

/**
 * Created by bogda on 29/11/2015.
 */
public class TestUtilities extends AndroidTestCase{
    private static String TAG = "chess-data";

    static ContentValues createProfileValues (String profileId){
        ContentValues profileValues = new ContentValues();
        profileValues.put(ProfileEntry.COLUMN_PROFILE_ID,profileId);
        profileValues.put(ProfileEntry.COLUMN_EMAIL,"testmail@mail.com");
        profileValues.put(ProfileEntry.COLUMN_NAME,"Test Name");
        Date dateValue = new Date();
        long date = dateValue.getTime();
        profileValues.put(ProfileEntry.COLUMN_DATE_DATE_OF_BIRTH,date);
        profileValues.put(ProfileEntry.COLUMN_ELO,0);
        profileValues.put(ProfileEntry.COLUMN_ALT_ELO,0);
        profileValues.put(ProfileEntry.COLUMN_DATE_CREATED,date);
        profileValues.put(ProfileEntry.COLUMN_UPATE_STAMP,date);
        return profileValues;
    }

    static ContentValues createProfileVipValues(){
        ContentValues profileValues = new ContentValues();
        profileValues.put(ProfileEntry.COLUMN_PROFILE_ID,"profileVip1");
        profileValues.put(ProfileEntry.COLUMN_EMAIL,"testmail@mail.com");
        profileValues.put(ProfileEntry.COLUMN_NAME,"TestProfile Where is the bug?");
        Date dateValue = new Date();
        long date = dateValue.getTime();
        profileValues.put(ProfileEntry.COLUMN_DATE_DATE_OF_BIRTH,date);
        profileValues.put(ProfileEntry.COLUMN_ELO,0);
        profileValues.put(ProfileEntry.COLUMN_ALT_ELO,0);
        profileValues.put(ProfileEntry.COLUMN_DATE_CREATED,date);
        profileValues.put(ProfileEntry.COLUMN_UPATE_STAMP,date);
        return profileValues;
    }

    static long insertProfileValues(Context context, ContentValues testValues){
        ChessDataDbHelper dbHelper = new ChessDataDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long profileRowId;
        profileRowId = db.insert(ProfileEntry.TABLE_NAME,null,testValues);

        //verify we got a row back.
        assertTrue("Error: Failure to insert vip profile values", profileRowId != -1);
        return profileRowId;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);

            String cursorValue = valueCursor.getString(idx);
            String expectedValue = entry.getValue().toString();

            Log.d(TAG,"bogdan:   cursorValue = " + cursorValue);
            Log.d(TAG,"bogdan: expectedValue = " + expectedValue);
            assertEquals("Value '" + valueCursor.getString(idx) +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
