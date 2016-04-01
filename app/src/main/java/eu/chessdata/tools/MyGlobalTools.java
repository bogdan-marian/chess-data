package eu.chessdata.tools;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.chessdata.backend.tournamentEndpoint.TournamentEndpoint;
import eu.chessdata.backend.tournamentEndpoint.model.TournamentPlayer;
import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.data.simplesql.ProfileTable;
import eu.chessdata.data.simplesql.TournamentPlayerTable;
import eu.chessdata.data.simplesql.TournamentTable;

/**
 * It contains globally shared objects and utility tools that are useful all over the project
 * Created by bogdan on 29/11/2015.
 */
public class MyGlobalTools {
    public static final String ROOT_URL = "https://chess-data.appspot.com/_ah/api/";
    public static Map<String, Long> managedClubs;
    public static Map<Long, String> profileNames;
    /**
     * first string is the profile id from datastore
     * second string is the name of the profile
     */
    public static Map<String, String> memberSqlIdToProfileName = new HashMap<>();
    private static String TAG = "my-debug-tag";
    private static TournamentEndpoint tournamentEndpoint = buildTournamentEndpoint();

    public static void addToMembersSqlIdToProfileName(String id, String name) {
        memberSqlIdToProfileName.put(id, name);
    }

    public static String getNameByProfileId(String profileId) {
        return memberSqlIdToProfileName.get(profileId);
    }

    /**
     * It looks in the sqlite database and returns the cloud tournamentId
     *
     * @param tournamentSqlId
     * @param contentResolver
     * @return
     */
    public static Long getTournamentCloudIdBySqlId(Long tournamentSqlId, ContentResolver contentResolver) {
        String selection = TournamentTable.FIELD__ID + " = ?";
        String[] selectionArguments = {tournamentSqlId.toString()};
        Cursor cursor = contentResolver.query(
                TournamentTable.CONTENT_URI,
                null,
                selection,
                selectionArguments,
                null
        );
        int idxTournamentId = cursor.getColumnIndex(TournamentTable.FIELD_TOURNAMENTID);

        cursor.moveToFirst();
        long tournamentId = cursor.getLong(idxTournamentId);
        cursor.close();
        return tournamentId;
    }

    /**
     * It looks in the sqlite database and returns the cloud profileId
     *
     * @param profileSqlId
     * @param contentResolver
     * @return
     */
    public static String getProfileCloudIdBySqlId(Long profileSqlId, ContentResolver contentResolver) {
        String selection = ProfileTable.FIELD__ID + " =?";
        String[] selectionArguments = {profileSqlId.toString()};
        Cursor cursor = contentResolver.query(
                ProfileTable.CONTENT_URI,
                null,
                selection,
                selectionArguments,
                null
        );
        int idxProfileId = cursor.getColumnIndex(ProfileTable.FIELD_PROFILEID);
        cursor.moveToFirst();
        String profileId = cursor.getString(idxProfileId);
        cursor.close();
        return profileId;
    }

    /**
     * Very long running task.
     * Its syncing local created tournament players.
     * It looks in the database and tries to find tournament players that do not have
     * {@code tournamentPlayerId } populated. This means that they are not synced to the cloud.
     * After locating them it tries to populate them on the cloud.
     */
    public static void syncLocalTournamentPlayers(ContentResolver contentResolver, String idTokenString) {

        String[] TOURNAMENTPLAYER_COLUMNS = {
                TournamentPlayerTable.FIELD__ID,
                TournamentPlayerTable.FIELD_TOURNAMENTPLAYERID,
                TournamentPlayerTable.FIELD_TOURNAMENTID,
                TournamentPlayerTable.FIELD_PROFILEID,
                TournamentPlayerTable.FIELD_DATECREATED,
                TournamentPlayerTable.FIELD_UPDATESTAMP
        };

        int IDX_TOURNAMENT_PLAYER_ID = 1;
        int IDX_TOURNAMENT_ID = 2;
        int IDX_PROFILE_ID = 3;
        int IDX_DATE_CREATED = 4;
        int IDX_UPDATE_STAMP = 5;

        String selection = TournamentPlayerTable.FIELD_TOURNAMENTPLAYERID + " is null";
        Cursor cursor = contentResolver.query(
                TournamentPlayerTable.CONTENT_URI,
                null,
                selection,
                null,
                null
        );
        while (cursor.moveToNext()) {
            //now wee have a tournament player that wee need to send to the endpoints
            TournamentPlayer tournamentPlayer = new TournamentPlayer();
            tournamentPlayer.setTournamentId(cursor.getLong(IDX_TOURNAMENT_ID));
            tournamentPlayer.setProfileId(cursor.getString(IDX_PROFILE_ID));
            tournamentPlayer.setDateCreated(cursor.getLong(IDX_DATE_CREATED));
            tournamentPlayer.setUpdateStamp(cursor.getLong(IDX_UPDATE_STAMP));

            try {
                TournamentPlayer vipPlayer = tournamentEndpoint.tournamentAddPlayer(idTokenString,
                        tournamentPlayer).execute();
                if (vipPlayer != null) {
                    String hackMessage = vipPlayer.getProfileId();
                    String message = hackMessage.split(":")[0];
                    if (message.equals("Not created")) {
                        Log.d(TAG, "Something happened: " + hackMessage);
                        //TODO delete the selected player from local sqlite
                        continue;
                    }

                    //update the current tournamentPlayer
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TournamentPlayerTable.FIELD_TOURNAMENTPLAYERID, vipPlayer.getTournamentPlayerId());
                    contentValues.put(TournamentPlayerTable.FIELD_DATECREATED, vipPlayer.getDateCreated());
                    contentValues.put(TournamentPlayerTable.FIELD_UPDATESTAMP, vipPlayer.getUpdateStamp());
                    String selectionB = TournamentPlayerTable.FIELD__ID + " =?";
                    Long myId = cursor.getLong(0);
                    String selectionArgsB[] = {myId.toString()};
                    int rowsUpdated = contentResolver.update(
                            TournamentPlayerTable.CONTENT_URI,
                            contentValues,
                            selectionB,
                            selectionArgsB);
                    if (rowsUpdated != 1) {
                        Log.d(TAG, "Error You should only update one row :)");
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "Not able to update to cloud local tournamentPlayer: " + e);
            }
        }
        cursor.close();
    }

    private static TournamentEndpoint buildTournamentEndpoint() {
        TournamentEndpoint.Builder builder =
                new TournamentEndpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setRootUrl(MyGlobalTools.ROOT_URL);
        return builder.build();
    }

    public static int getTournamentTotalRounds(ContentResolver contentResolver, String stringTournamentUri) {
        Uri paramTournamentUri = Uri.parse(stringTournamentUri);
        String tournamentSqlId = paramTournamentUri.getLastPathSegment();

        Uri tournamentUri = TournamentTable.CONTENT_URI;
        String selection = TournamentTable.FIELD__ID + " =?";
        String args[] = {tournamentSqlId};
        Cursor cursor = contentResolver.query(tournamentUri, null, selection, args, null);
        int returnVal = 0;
        int idx_TotalRounds = cursor.getColumnIndex(TournamentTable.FIELD_TOTALROUNDS);
        while (cursor.moveToNext()) {
            returnVal = cursor.getInt(idx_TotalRounds);
        }
        cursor.close();
        return returnVal;
    }

    public static String getTournamentNameTournamentId(Long tournamentId, ContentResolver contentResolver) {
        Uri uri = TournamentTable.CONTENT_URI;
        String projection[] = {TournamentTable.FIELD_NAME};
        int idx_Name = 0;
        String selection = TournamentTable.FIELD_TOURNAMENTID + " =?";
        String args[] = {tournamentId.toString()};

        Cursor cursor = contentResolver.query(uri, projection, selection, args, null);
        String name = "Tournament not found";
        while (cursor.moveToNext()) {
            name = cursor.getString(idx_Name);
        }
        cursor.close();
        return name;
    }

    public static boolean profileCanManageClubByTournamentId(ContentResolver contentResolver, String profileId, String tournamentId) {
        Uri tournamentUri = TournamentTable.CONTENT_URI;
        String tournamentProjection[] = {TournamentTable.FIELD_CLUBID};
        String tournamentSelection = TournamentTable.FIELD_TOURNAMENTID + " =?";
        String tournamentArgs[] = {tournamentId};

        Cursor tournamentCursor = contentResolver.query(tournamentUri, tournamentProjection, tournamentSelection, tournamentArgs, null);
        if (!tournamentCursor.moveToNext()) {
            //this should allays be able to locate the tournament;
            String problem = "You passed an illegal tournament id: " + tournamentId;
            Log.e(TAG, "You passed an illegal tournament id: " + tournamentId);
            throw new IllegalStateException(problem);
        }

        String clubId = String.valueOf(tournamentCursor.getLong(0));
        tournamentCursor.close();


        Uri memberUri = ClubMemberTable.CONTENT_URI;
        String memberSelection = ClubMemberTable.FIELD_CLUBID + " =? AND " +
                ClubMemberTable.FIELD_PROFILEID + " =? AND " +
                ClubMemberTable.FIELD_MANAGERPROFILE + " =? ";
        String memberArgs[] = {clubId, profileId, "1"};

        Cursor memberCursor = contentResolver.query(memberUri, null, memberSelection, memberArgs, null);
        int count = memberCursor.getCount();
        memberCursor.close();
        if (count == 1) {
            return true;
        }
        return false;
    }
}
