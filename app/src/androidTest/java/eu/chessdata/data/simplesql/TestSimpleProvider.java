package eu.chessdata.data.simplesql;

import android.content.ContentResolver;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import eu.chessdata.tools.Params;

/**
 * Created by bogda on 07/12/2015.
 */
public class TestSimpleProvider extends AndroidTestCase {
    private String TAG = "my-debug-tag";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
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
        assertEquals("Error: Not able to delete from profile ", 0, cursor.getCount());
        cursor.close();

        mContext.getContentResolver().delete(
                ClubTable.CONTENT_URI,
                null,
                null
        );
        cursor = mContext.getContentResolver().query(
                ClubTable.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Not able to delete from ClubTable ", 0, cursor.getCount());
        cursor.close();
    }

    public void test1ProfileInsertVip(){
        ProfileSql vip = SimpleUtilities.createProfileVipValues();
        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.insert(ProfileTable.CONTENT_URI, ProfileTable.getContentValues(vip, false));

        Cursor cursor = contentResolver.query(ProfileTable.CONTENT_URI,null,null,null,null);
        ProfileSql profile1 = ProfileTable.getRow(cursor, true);
        SimpleUtilities.compareProfilesNoId(vip, profile1);
    }

    public void test2ReadProfile(){
        test1ProfileInsertVip();
        ProfileSql vip = SimpleUtilities.createProfileVipValues();
        ContentResolver contentResolver = mContext.getContentResolver();

        //String mSelectionClause = null;
        String[] mSelectionArgs = {""};

        String mSelectionClause = ProfileTable.FIELD_PROFILEID + " = ?";
        mSelectionArgs[0]= vip.profileId;

        String mSortOrder =  ProfileTable.FIELD__ID +" ASC";

        Cursor cursor = contentResolver.query(ProfileTable.CONTENT_URI,
                null,
                mSelectionClause,
                mSelectionArgs,
                null);
        assertNotNull("Cursor is null ", cursor);
        int count = cursor.getCount();
        Log.d(TAG,"Cursor count = " + count);
        assertTrue("Cursor query did not returned values ", cursor.getCount() > 0);
    }

    public void test3ReadProfileUsingToolsParams(){
        test1ProfileInsertVip();
        ProfileSql vip = SimpleUtilities.createProfileVipValues();
        ContentResolver contentResolver = mContext.getContentResolver();
        Params params = Params.getProfileById(vip.profileId);

        Cursor cursor = contentResolver.query(
                params.getUri(),
                params.getProjection(),
                params.getSelection(),
                params.getSelectionArgs(),
                params.getSortOrder());
        assertNotNull("Cursor is null ", cursor);
        assertTrue("Cursor query did not returned values ", cursor.getCount() > 0);
    }

    /*public void test4ClubInsert(){
        ClubSql vip = SimpleUtilities.createClubVipValues();
        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.insert(ClubTable.CONTENT_URI, ClubTable.getContentValues(vip, false));

        Cursor cursor = contentResolver.query(ClubTable.CONTENT_URI, null, null, null, null);
        ClubSql club1 = ClubTable.getRow(cursor, true);
        SimpleUtilities.compareClubsNoId(vip, club1);
    }*/
}
