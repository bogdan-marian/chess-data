package eu.chessdata.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.util.Date;

import eu.chessdata.R;
import eu.chessdata.backend.profileEndpoint.ProfileEndpoint;
import eu.chessdata.backend.profileEndpoint.model.ClubMember;
import eu.chessdata.backend.profileEndpoint.model.Profile;
import eu.chessdata.data.simplesql.ClubMemberSql;
import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.data.simplesql.ClubTable;
import eu.chessdata.data.simplesql.ProfileSql;
import eu.chessdata.data.simplesql.ProfileTable;
import eu.chessdata.tools.MyGlobalSharedObjects;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ProfileService extends IntentService {
    private static boolean sNamesUpdated = false;
    private static GsonFactory sGsonFactory = new GsonFactory();
    private static ProfileEndpoint profileEndpoint = buildProfileEndpoint();

    private String TAG = "my-debug-tag";
    private SharedPreferences mSharedPreferences;
    private ContentResolver mContentResolver;
    private String mIdTokenString;
    private Long mClubEndpointId;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    private static final String ACTION_CREATE_VIRTUAL_PROFILE = "eu.chessdata.services.action.create.virtual.profile";
    private static final String ACTION_UPDATE_ALL_MEMBERS_MAP = "eu.chessdata.services.action.update.all.members.map";

    // TODO: Rename parameters
    private static final String EXTRA_JSON_VIRTUAL_PROFILE = "eu.chessdata.services.extra.json.virtualProfile";
    private static final String EXTRA_PARAM2 = "eu.chessdata.services.extra.PARAM2";

    public ProfileService() {
        super("ProfileService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionCreateVirtualProfile(Context context, Profile virtualProfile) {
        String jsonVirtualProfile = serializeVirtualProfile(virtualProfile);

        Intent intent = new Intent(context, ProfileService.class);
        intent.setAction(ACTION_CREATE_VIRTUAL_PROFILE);
        intent.putExtra(EXTRA_JSON_VIRTUAL_PROFILE, jsonVirtualProfile);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpdateAllMembersMap(Context context) {
        Intent intent = new Intent(context, ProfileService.class);
        intent.setAction(ACTION_UPDATE_ALL_MEMBERS_MAP);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mSharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            mContentResolver = getContentResolver();
            mIdTokenString = mSharedPreferences.getString(
                    getString(R.string.pref_security_id_token_string), "defaultValue");

            long clubSqlId = mSharedPreferences.getLong(
                    getString(R.string.pref_managed_club_sqlId), 0L);
            mClubEndpointId = getClubEndpointId(clubSqlId);

            final String action = intent.getAction();
            if (ACTION_CREATE_VIRTUAL_PROFILE.equals(action)) {
                final String jsonVirtualProfile = intent.getStringExtra(EXTRA_JSON_VIRTUAL_PROFILE);
                handleActionCreateVirtualProfile(jsonVirtualProfile);
            } else if (ACTION_UPDATE_ALL_MEMBERS_MAP.equals(action)) {
                handleActionUpdateAllMembersMap();
            }
        }
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCreateVirtualProfile(String jsonVirtualProfile) {
        Profile virtualProfile = deserializeVirtualProfile(jsonVirtualProfile);
        Profile profile = deserializeVirtualProfile(jsonVirtualProfile);

        if (profile != null && mClubEndpointId != null) {

            try {
                Profile vipProfile = profileEndpoint
                        .createVirtualProfile(mClubEndpointId, mIdTokenString, profile)
                        .execute();

                if (vipProfile == null){
                    Log.d(TAG, "Something when wrong: null vipProfile: ");
                    return;
                }
                String[] notCreated = vipProfile.getName().split(": ");
                if (notCreated[0].equals("Not created")){
                    Log.d(TAG,"Found chess-data-error: " + vipProfile.getName());
                    return;
                }

                //create the profile on sqlite
                ProfileSql profileSql = new ProfileSql(vipProfile);
                Uri newUri = mContentResolver.insert(
                        ProfileTable.CONTENT_URI,
                        ProfileTable.getContentValues(profileSql,false)
                );
                if (ContentUris.parseId(newUri)<0){
                    Log.d(TAG,"Not able to store Profile in the database");
                    return;
                }
                Log.d(TAG,"newUri for ProfileTable uri: "+newUri.toString());

                //get the member id from endpoints
                ClubMember member = profileEndpoint.getJustCreatedVirtualMember
                        (mIdTokenString,vipProfile.getProfileId(),mClubEndpointId).execute();
                if (member.getProfileId().split(":").equals("Illegal request")){
                    Log.d(TAG,"Something is not write" +member.getProfileId());
                    return;
                }

                Log.d(TAG,"Wee have the member data"+member.getProfileId());

                //store member in sqlite
                ClubMemberSql memberSql = new ClubMemberSql(member);
                Uri memberUri = mContentResolver.insert(
                        ClubMemberTable.CONTENT_URI,
                        ClubMemberTable.getContentValues(memberSql,false)
                );
                if (ContentUris.parseId(memberUri)<0){
                    Log.d(TAG,"not able to store member data in sqlite");
                    return;
                }

                //debug section
                Cursor cursor = mContentResolver.query(
                        ClubMemberTable.CONTENT_URI,
                        null,//projection
                        null,//selection
                        null ,//selection args
                        null //sort order
                );
                Log.d(TAG,"Total club members = " + cursor.getCount());
            } catch (IOException e) {
                Log.d(TAG, "Something when wrong: handleActionCreateVirtualProfile: ");
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateAllMembersMap() {
        if (sNamesUpdated){
            return;
        }
        Log.d(TAG,"OK handleActionUpdateAllMembersMap: initialized");
        // ClubMemberTable.FIELD_CLUBMEMBERID +" IS NOT NULL"
        Cursor membersCursor = mContentResolver.query(ClubMemberTable.CONTENT_URI,
                null,//projection
                ClubMemberTable.FIELD_CLUBMEMBERID +" IS NOT NULL",//selection
                null ,//selection args
                null //sort order
        );
        if (membersCursor == null){
            Log.d(TAG,"no members found");
            return;
        }

        int idx_id = membersCursor.getColumnIndex(ClubMemberTable.FIELD_PROFILEID);
        int i=0;
        while (membersCursor.moveToNext()){


            String id = membersCursor.getString(idx_id);
            if (id != null) {
                String name = getNameById(id);
                MyGlobalSharedObjects.addToMembersSqlIdToProfileName(id, name);
            }
            i++;
        }
        membersCursor.close();
        sNamesUpdated = true;
    }

    private static String serializeVirtualProfile(Profile virtualProfile) {
        try {
            String jsonVirtualProfile = sGsonFactory.toString(virtualProfile);
            return jsonVirtualProfile;
        } catch (IOException e) {
            throw new IllegalStateException("Not able to create jsonVirtualProfile ");
        }
    }

    private static Profile deserializeVirtualProfile(String jsonString) {
        try {
            Profile virtualProfile =
                    sGsonFactory.fromString(jsonString, Profile.class);
            return virtualProfile;
        } catch (IOException e) {
            Log.d("my-debug-tag", "Not able to deserializeVirtualProfile");
            throw new IllegalStateException("Not able to deserializeVirtualProfile");
        }
    }

    private static ProfileEndpoint buildProfileEndpoint() {
        ProfileEndpoint.Builder builder =
                new ProfileEndpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setRootUrl(MyGlobalSharedObjects.ROOT_URL);
        return builder.build();
    }

    private Long getClubEndpointId(long clubSqlId) {
        Uri clubUri = ClubTable.CONTENT_URI;
        String[] projection = {
                ClubTable.FIELD__ID,
                ClubTable.FIELD_CLUBID
        };
        int COL_CLUBID = 1;
        String selection = ClubTable.FIELD__ID + " = ?";
        String stringClubSqlId = Long.toString(clubSqlId);
        String[] selectionArguments = {stringClubSqlId};

        Cursor cursor = mContentResolver.query(clubUri, projection, selection, selectionArguments, null);
        int count = cursor.getCount();
        cursor.moveToFirst();
        long endPointId = cursor.getLong(COL_CLUBID);
        return endPointId;
    }
    private String getNameById(String profileId){
        String [] arguments = {profileId};
        Cursor profileCursor = mContentResolver.query(
                ProfileTable.CONTENT_URI,
                null,
                ProfileTable.FIELD_PROFILEID  +"= ?",
                arguments,
                null
        );

        if(!(profileCursor != null && profileCursor.moveToFirst())){
            return "Not able to locate profile id: "+ profileId;
        }
        int idx_profileName = profileCursor.getColumnIndex(ProfileTable.FIELD_NAME);

        String name = profileCursor.getString(idx_profileName);
        profileCursor.close();
        return name;

        //return "no more crashes?";
    }
}
