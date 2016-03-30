package eu.chessdata.round;

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
 * Created by Bogdan Oloeriu on 30/03/2016.
 */
public class RoundAddPlayerAdapter extends CursorAdapter{
    private int mIdx_profileId = -1;
    public RoundAddPlayerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_text, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (mIdx_profileId == -1) {
            mIdx_profileId = cursor.getColumnIndex(TournamentPlayerTable.FIELD_PROFILEID);
        }
        TextView textView = (TextView) view.findViewById(R.id.list_item_text_general_view);
        String  profileId = cursor.getString(mIdx_profileId);

        textView.setText(MyGlobalTools.getNameByProfileId(profileId));

    }
}
