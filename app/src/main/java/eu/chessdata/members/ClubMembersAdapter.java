package eu.chessdata.members;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import eu.chessdata.R;
import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.tools.MyGlobalSharedObjects;

/**
 * Created by Bogdan Oloeriu on 01/02/2016.
 */
public class ClubMembersAdapter extends CursorAdapter {
    public ClubMembersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_club_member,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView)view.findViewById(R.id.list_item_member_data);
        textView.setText(getCursorData(cursor));
    }

    private String getCursorData(Cursor cursor){
        int idxProfileName = cursor.getColumnIndex(ClubMemberTable.FIELD__ID);
        int idxProfileId = cursor.getColumnIndex(ClubMemberTable.FIELD_PROFILEID);
        String profileId =cursor.getString(idxProfileId);
        String name = MyGlobalSharedObjects.getNameByProfileId(profileId);
        return name;
    }
}
