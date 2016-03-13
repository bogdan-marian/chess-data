package eu.chessdata.services;

import android.content.ContentResolver;
import android.database.Cursor;

import eu.chessdata.data.simplesql.TournamentTable;

/**
 * Created by Bogdan Oloeriu on 03/03/2016.
 */
public class LocalIdToDatastoreId {
    public static long getTournamentId(Long tournamentSqlId, ContentResolver contentResolver) {
        String[] arguments = {tournamentSqlId.toString()};
        Cursor cursor = contentResolver.query(
                TournamentTable.CONTENT_URI,
                null,
                TournamentTable.FIELD__ID + "=?",
                arguments,
                null
        );
        boolean foundErrors = false;
        if (cursor == null) {
            return -1;
        }
        if (!cursor.moveToFirst()) {
            return -1;
        }
        int idx_tournamentId = cursor.getColumnIndex(TournamentTable.FIELD_TOURNAMENTID);
        long tournamentId = cursor.getLong(idx_tournamentId);
        cursor.close();
        return tournamentId;
    }
}
