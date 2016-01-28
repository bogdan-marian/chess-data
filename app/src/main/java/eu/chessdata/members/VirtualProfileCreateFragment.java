package eu.chessdata.members;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import eu.chessdata.R;
import eu.chessdata.backend.virtualProfileEndpoint.model.Email;
import eu.chessdata.backend.virtualProfileEndpoint.model.VirtualProfile;
import eu.chessdata.services.ProfileService;

/**
 * Created by Bogdan Oloeriu on 28/01/2016.
 */
public class VirtualProfileCreateFragment extends DialogFragment{
    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.fragment_virtual_profile_create,null);

        builder.setView(mView);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VirtualProfileCreateFragment.this.getDialog().cancel();
            }
        });

        builder.setPositiveButton("Create virtual profile", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VirtualProfile virtualProfile = buildVirtualProfile();
                ProfileService.startActionCreateVirtualProfile(getContext(),virtualProfile);
            }
        });

        return builder.create();
    }

    private VirtualProfile buildVirtualProfile(){
        VirtualProfile profile = new VirtualProfile();

        profile.setName(((EditText) mView.findViewById(R.id.profileName)).getText().toString());

        String emailValue = ((EditText)mView.findViewById(R.id.email)).getText().toString();
        profile.setEmail(new Email().setEmail(emailValue));

        String intText = ((EditText) mView.findViewById(R.id.elo)).getText().toString();
        int elo = Integer.parseInt(intText);
        profile.setElo(elo);

        return  profile;
    }
}
