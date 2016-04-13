package eu.chessdata.round;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 13/04/2016.
 */
public class GameSetResultDialog extends DialogFragment {
    private ContentResolver mContentResolver;
    private String mGameSqlId;
    private String mWhitePlayer;
    private String mBlackPlayer;
    private boolean mNoPartner = false;

    public static GameSetResultDialog newInstance(String gameSqlId, ContentResolver contentResolver) {
        GameSetResultDialog dialog = new GameSetResultDialog();
        dialog.mGameSqlId = gameSqlId;
        dialog.mContentResolver = contentResolver;
        dialog.updatePlayerNames();
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mNoPartner) {
            dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Who is the winner?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User cancelled the dialog
            }
        });
        final CharSequence[] items = {mWhitePlayer, mBlackPlayer, "1/2 - 1/2"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("my-debug-tag", "Item = " + which);
                dismiss();
            }
        });
        return builder.create();
    }

    private void updatePlayerNames() {
        Uri uri = GameTable.CONTENT_URI;
        String[] projection = {
                GameTable.FIELD_WHITEPLAYERID,
                GameTable.FIELD_BLACKPLAYERID,
                GameTable.FIELD_RESULT
        };
        int idx_white = 0;
        int idx_black = 1;
        int idx_result = 2;
        String selection = GameTable.FIELD__ID + " =?";
        String[] selectionArgs = {mGameSqlId};
        Cursor cursor = mContentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );
        if (cursor.getCount() != 1) {
            String problem = "Not able to find the game by id: " + mGameSqlId;
            throw new IllegalStateException(problem);
        }
        cursor.moveToFirst();
        int result = cursor.getInt(idx_result);
        if (result == 4) {
            mNoPartner = true;
            return;
        }

        mWhitePlayer = "White: " + MyGlobalTools.getNameByProfileId(cursor.getString(idx_white));
        mBlackPlayer = "Black: " + MyGlobalTools.getNameByProfileId(cursor.getString(idx_black));
    }
}
