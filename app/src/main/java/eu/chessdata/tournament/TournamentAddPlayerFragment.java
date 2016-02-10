package eu.chessdata.tournament;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Bogdan Oloeriu on 10/02/2016.
 */
public class TournamentAddPlayerFragment extends DialogFragment{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add player to tournament");


        return super.onCreateDialog(savedInstanceState);
    }
}
