package eu.chessdata.tournament;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import eu.chessdata.TournamentDetailsFragment;

/**
 * Created by Bogdan Oloeriu on 10/02/2016.
 */
public class TournamentAddPlayerFragment extends DialogFragment{
    private String TAG = "my-debug-tag";
    private AlertDialog mAlertDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String stringUri = getArguments().getString(TournamentDetailsFragment.TOURNAMENT_URI);
        Log.d(TAG, "Add player for: " + stringUri);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select player to add");

        mAlertDialog = builder.create();
        return mAlertDialog;
    }
}
