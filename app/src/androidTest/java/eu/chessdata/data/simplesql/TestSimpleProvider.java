package eu.chessdata.data.simplesql;

import android.content.ContentResolver;
import android.database.Cursor;
import android.test.AndroidTestCase;

/**
 * Created by bogda on 07/12/2015.
 */
public class TestSimpleProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    private void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ProfileTable.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                ProfileTable.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Not able to delete from profile ",0, cursor.getCount());
        cursor.close();
    }

    public void test1ProfileInsertVip(){
        Profile vip = SimpleUtilities.createProfileVipValues();
        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.insert(ProfileTable.CONTENT_URI, ProfileTable.getContentValues(vip,false));

        Cursor cursor = contentResolver.query(ProfileTable.CONTENT_URI,null,null,null,null);
        Profile profile1 = ProfileTable.getRow(cursor, true);
        assertTrue("Profile not the same ", true);

        SimpleUtilities.compareProfilesNoId(vip,profile1);
    }
}
