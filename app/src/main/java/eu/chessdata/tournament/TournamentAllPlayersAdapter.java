package eu.chessdata.tournament;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.TournamentPlayerTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 12/03/2016.
 */
public class TournamentAllPlayersAdapter extends CursorAdapter{

    public TournamentAllPlayersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_text,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView)view.findViewById(R.id.list_item_text_general_view);
        textView.setText(getCursorData(cursor));
    }

    /**
     * it is important that you make sure that the profile id is cached before accessing this
     * method
     * @param cursor
     * @return
     */
    private String getCursorData(Cursor cursor) {
        int idx_profileId = cursor.getColumnIndex(TournamentPlayerTable.FIELD_PROFILEID);
        String val_profileId = cursor.getString(idx_profileId);
        return MyGlobalTools.getNameByProfileId(val_profileId);
    }
}
