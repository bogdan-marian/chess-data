package eu.chessdata.round;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import eu.chessdata.services.TournamentService;

/**
 * Created by Bogdan Oloeriu on 07/04/2016.
 */
public class CreateGamesDialog extends DialogFragment{
    private String mRoundId;
    private RoundStateFragment mRoundStateFragment;

    public static CreateGamesDialog newInstance (String roundId, RoundStateFragment roundStateFragment){
        CreateGamesDialog dialog = new CreateGamesDialog();
        dialog.mRoundId = roundId;
        dialog.mRoundStateFragment = roundStateFragment;
        return  dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Create games?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //todo generate games
                TournamentService.startActionGenerateGames(getContext(), mRoundId);
                mRoundStateFragment.showGames();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User cancelled the dialog
            }
        });
        return builder.create();
    }
}
