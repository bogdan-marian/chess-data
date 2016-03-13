package eu.chessdata;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.Map;

import eu.chessdata.tools.MyGlobalTools;

/**
 * Created by bogda on 18/01/2016.
 */
public class DeviceSetDefaultManagedClub  extends DialogFragment    {
    private String TAG = "my-debug-tag";
    private SharedPreferences mSharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mSharedPreferences = super.getContext().getSharedPreferences(
                getString(R.string.preference_file_key),Context.MODE_PRIVATE
        );
        final SharedPreferences.Editor editor = mSharedPreferences.edit();

        final Map<String,Long> map = MyGlobalTools.managedClubs;
        final String[]items = new String[map.size()];
        int i =0;
        for (Map.Entry<String,Long> item:map.entrySet()){
            items[i++]= item.getKey();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please select default club");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                editor.putString(getString(R.string.pref_managed_club_name), items[which]);
                editor.putLong(getString(R.string.pref_managed_club_sqlId), map.get(items[which]));
                editor.commit();
            }
        });

        return builder.create();
    }
}