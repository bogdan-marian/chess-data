package eu.chessdata.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import eu.chessdata.data.ChessDataContract.ProfileEntry;
/**
 * Created by bogda on 03/12/2015.
 */
public class TestProvider extends AndroidTestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void deleteAllRecords(){
        deleteAllRecordsFromDB();
    }

    public void deleteAllRecordsFromDB() {
        ChessDataDbHelper dbHelper = new ChessDataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(ProfileEntry.TABLE_NAME, null,null);
        db.close();
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

}
