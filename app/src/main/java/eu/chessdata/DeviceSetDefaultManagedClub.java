package eu.chessdata;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.chessdata.tools.MyGlobalSharedObjects;

/**
 * Created by bogda on 18/01/2016.
 */
public class DeviceSetDefaultManagedClub  extends DialogFragment    {
    private String TAG = "my-debug-tag";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Map<String,Long> map = MyGlobalSharedObjects.managedClubs;
        final String[]items = new String[map.size()];
        int i =0;
        for (Map.Entry<String,Long> item:map.entrySet()){
            items[i++]= item.getKey();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please slect default club");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG,"Item cliked "+ items[which]
                    + "And id = " + map.get(items[which]));
            }
        });

        return builder.create();
    }
}