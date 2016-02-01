package eu.chessdata.members;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.data.simplesql.ProfileTable;

/**
 * Created by Bogdan Oloeriu on 01/02/2016.
 */
public class ClubMembersAdapter extends CursorAdapter {
    public ClubMembersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    private String getCursorData(Cursor cursor){
        int idxProfileName = cursor.getColumnIndex(ClubMemberTable.FIELD_CLUBMEMBERID);
        String profileName ="id: " +cursor.getLong(idxProfileName);
        return profileName;
    }
}
