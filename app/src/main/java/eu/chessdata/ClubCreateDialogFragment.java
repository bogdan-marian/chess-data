package eu.chessdata;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by bogda on 17/12/2015.
 */
public class ClubCreateDialogFragment extends DialogFragment {
    private String TAG = "my-debug-tag";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.club_create_dialog,null);
        final EditText name = (EditText)view.findViewById(R.id.clubName);
        final EditText description = (EditText)view.findViewById(R.id.clubDescription);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create_club, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String message = "Club name = " + name.getText() +
                                "\nClub description = " + description.getText();
                        Log.d(TAG, message);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClubCreateDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /*class CreateClub extends AsyncTask<Club,String,String>{

    }*/
}
