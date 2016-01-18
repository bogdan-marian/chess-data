package eu.chessdata;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bogda on 18/01/2016.
 */
public class DeviceSetDefaultManagedClub extends DialogFragment {
    private String TAG = "my-debug-tag";
    private View mView;
    private SharedPreferences mSharedPreferences;


    public Dialog onCreateDialog (Bundle savedInstanceState){
        List<String> list = new ArrayList<>();
        list.add("Bogdan");
        list.add("Marian");
        list.add("Oloeriu");
        final String[] items = new String[list.size()];
        for (int i=0;i<list.size();i++){
            items[i]=list.get(i);
        }

        mSharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What club would you like to manage?")
            .setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Log.d(TAG,"Default club will be: " + items[which]);
                }
            });

        return builder.create();
    }

    class SetManagedClub extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }
    }
}
