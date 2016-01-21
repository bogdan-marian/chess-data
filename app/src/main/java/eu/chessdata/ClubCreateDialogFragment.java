package eu.chessdata;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import eu.chessdata.backend.clubEndpoint.ClubEndpoint;
import eu.chessdata.backend.clubEndpoint.model.Club;
import eu.chessdata.backend.clubEndpoint.model.Email;
import eu.chessdata.backend.clubEndpoint.model.Link;
import eu.chessdata.data.simplesql.ClubSql;
import eu.chessdata.data.simplesql.ClubTable;
import eu.chessdata.tools.MyGlobalSharedObjects;

/**
 * Created by bogda on 17/12/2015.
 */
public class ClubCreateDialogFragment extends DialogFragment {
    private String TAG = "my-debug-tag";
    private View mView;
    private SharedPreferences mSharedPreferences;
    private String mDefaultValue;
    private String mIdTokenString;
    private ContentResolver mContentResolver;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mView = inflater.inflate(R.layout.club_create_dialog, null);
        mSharedPreferences = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mDefaultValue = getString(R.string.pref_profile_signedOut);
        mIdTokenString = mSharedPreferences.getString(
                getString(R.string.pref_security_id_token_string), mDefaultValue);
        mContentResolver = getActivity().getContentResolver();

        final EditText name = (EditText) mView.findViewById(R.id.clubName);
        final EditText description = (EditText) mView.findViewById(R.id.clubDescription);
        builder.setView(mView)
                // Add action buttons
                .setPositiveButton(R.string.create_club, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String message = "Club name = " + name.getText() +
                                "\nClub description = " + description.getText();
                        //build the club
                        Club club = buildClub();
                        new SendClubToEndpoints().execute(club);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClubCreateDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private Club buildClub() {
        Club club = new Club();
        club.setName(((EditText) mView.findViewById(R.id.clubName)).getText().toString());
        club.setShortName(((EditText) mView.findViewById(R.id.shortName)).getText().toString());

        String emailVal = ((EditText) mView.findViewById(R.id.email)).getText().toString();
        Email email = new Email();
        email.setEmail(emailVal);
        club.setEmail(email);

        club.setCountry(((EditText) mView.findViewById(R.id.country)).getText().toString());
        club.setCity(((EditText) mView.findViewById(R.id.city)).getText().toString());

        String linkVal = ((EditText) mView.findViewById(R.id.homePage)).getText().toString();
        Link link = new Link();
        link.setValue(linkVal);
        club.setHomePage(link);

        club.setDescription(((EditText) mView.findViewById(R.id.clubDescription)).getText().toString());

        return club;
    }

    /**
     * it sends the data to the endpoints and sores the result in
     * local content provider
     */
    class SendClubToEndpoints extends AsyncTask<Club, String, String> {
        @Override
        protected String doInBackground(Club... params) {
            Log.d(TAG, "Start send to endpoints");
            Club club = params[0];

            ClubEndpoint.Builder builder =
                    new ClubEndpoint.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            null
                    ).setRootUrl(MyGlobalSharedObjects.ROOT_URL);
            ClubEndpoint clubEndpoint = builder.build();
            try {
                //vipClub is also stored in datastore
                Club vipClub = clubEndpoint.create(mIdTokenString,club).execute();
                //Store vipClub also in content provider
                if (vipClub != null){
                    //this is the line that actually inserts data in sqlite
                    Uri newUri = mContentResolver.insert(
                            ClubTable.CONTENT_URI, ClubTable.getContentValues(
                                    new ClubSql(vipClub), false
                            ));

                    //TODO find the club sqlId and store it in shared preferences
                    long sqlId = ContentUris.parseId(newUri);
                    String clubName = vipClub.getName();
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putLong(getString(R.string.pref_managed_club_sqlId),sqlId);
                    editor.putString(getString(R.string.pref_managed_club_name),clubName);
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
