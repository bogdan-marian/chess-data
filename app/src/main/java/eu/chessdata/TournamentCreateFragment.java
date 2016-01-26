package eu.chessdata;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Date;

import eu.chessdata.backend.tournamentEndpoint.model.Tournament;
import eu.chessdata.services.TournamentService;

/**
 * Created by Bogdan Oloeriu on 24/01/2016.
 */
public class TournamentCreateFragment extends DialogFragment{
    //private String TAG = "my-debug-tag";
    private View mView;
    private long mManagedClubId;
    //private SharedPreferences mSharedPreferences;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.fragment_tournament_create,null);
        builder.setView(mView);
        NumberPicker numberPicker = (NumberPicker)mView.findViewById(R.id.tournamentTotalRounds);
        numberPicker.setMinValue(3);
        numberPicker.setMaxValue(14);
        numberPicker.setValue(7);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );


        builder.setPositiveButton("Create tournament", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Tournament tournament = buildTournament();
                TournamentService.startActionCreateTournament(getContext(), tournament);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TournamentCreateFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    private Tournament buildTournament(){
        Tournament tournament = new Tournament();
        long time = (new Date()).getTime();

        tournament.setName(((EditText) mView.findViewById(R.id.tournamentName)).getText().toString());
        tournament.setDescription(((EditText) mView.findViewById(R.id.tournamentDescription)).getText().toString());
        tournament.setLocation(((EditText) mView.findViewById(R.id.tournamentLocation)).getText().toString());
        int value = ((NumberPicker)mView.findViewById(R.id.tournamentTotalRounds)).getValue();
        tournament.setTotalRounds(value);
        tournament.setStartDate(time);
        tournament.setEndDate(time);
        tournament.setUpdateStamp(time);
        return tournament;
    }
}
