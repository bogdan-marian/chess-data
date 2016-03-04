package eu.chessdata.tools;

import android.content.ContentResolver;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

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
     * It looks in the sqllite database and returns the cloud tournament id
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
}
