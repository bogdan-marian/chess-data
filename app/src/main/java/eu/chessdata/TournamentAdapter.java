package eu.chessdata;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessdata.data.simplesql.TournamentTable;

/**
 * Created by Bogdan Oloeriu on 21/01/2016.
 */
public class TournamentAdapter extends CursorAdapter {
    public TournamentAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_tournament,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView)view.findViewById(R.id.list_item_tournament_textView);
        tv.setText(getCursorData(cursor));
    }
    private String getCursorData(Cursor cursor){
        int idx_tournament_name = cursor.getColumnIndex(TournamentTable.FIELD_NAME);
        String value = cursor.getString(idx_tournament_name);
        return value;
    }
}
