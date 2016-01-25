package eu.chessdata;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import eu.chessdata.backend.tournamentEndpoint.model.Tournament;

/**
 * Created by Bogdan Oloeriu on 24/01/2016.
 */
public class TournamentCreateFragment extends DialogFragment{
    //private String TAG = "my-debug-tag";
    private View mView;
    //private SharedPreferences mSharedPreferences;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.fragment_tournament_create,null);
        builder.setView(mView);
        return builder.create();
    }

    private Tournament buildTournament(){
        Tournament tournament = new Tournament();
        return  tournament;
    }
}
