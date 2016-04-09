package eu.chessdata.round;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import eu.chessdata.R;
import eu.chessdata.data.simplesql.GameTable;
import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by Bogdan Oloeriu on 09/04/2016.
 */
public class RoundGamesAdapter extends CursorAdapter{
    public RoundGamesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_text,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.list_item_text_general_view);
    }

    private String getCursorData(Cursor cursor){
        int idx_whiteId = cursor.getColumnIndex(GameTable.FIELD_WHITEPLAYERID);
        int idx_blackId = cursor.getColumnIndex(GameTable.FIELD_BLACKPLAYERID);
        int idx_result = cursor.getColumnIndex(GameTable.FIELD_RESULT);
        int idx_tableNr = cursor.getColumnIndex(GameTable.FIELD_TABLENUMBER);
        StringBuilder sb = new StringBuilder();
        sb.append(cursor.getInt(idx_tableNr)+". ");
        sb.append(MyGlobalTools.getNameByProfileId(cursor.getString(idx_whiteId))+" vs ");
        sb.append(MyGlobalTools.getNameByProfileId(cursor.getString(idx_blackId))+" \n\t");
        sb.append(getResult(cursor.getInt(idx_result)));

        return sb.toString();
    }

    private String getResult(int result){
        switch (result){
            case 0:
                return "- - -";
            case 1:
                return "1 - 0";
            case 2:
                return "0 - 1";
            case 3:
                return "1/2 - 1/2";
        }
        throw new IllegalStateException("Invalid game result: " + result);
    }
}
