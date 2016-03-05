package eu.chessdata.tools;

import android.content.ContentResolver;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

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
     * It syncing local created tournament players.
     * It looks in the database and tries to find tournament players that do not have
     * {@code tournamentPlayerId } populated. This means that they are not synced to the cloud.
     * After locating them it tries to populate them on the cloud.
     */
    public static void syncLocalTournamentPlayers(ContentResolver contentResolver) {
        String selection = TournamentPlayerTable.FIELD_TOURNAMENTPLAYERID + " is null";
        Cursor cursor = contentResolver.query(
                TournamentPlayerTable.CONTENT_URI,
                null,
                selection,
                null,
                null
        );

        while (cursor.moveToNext()){

        }
    }
}
