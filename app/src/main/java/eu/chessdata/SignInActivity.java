package eu.chessdata;

import eu.chessdata.tools.MyGlobalSharedObjects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import eu.chessdata.backend.profileEndpoint.ProfileEndpoint;
import eu.chessdata.backend.profileEndpoint.model.Profile;
import eu.chessdata.backend.quoteEndpoint.QuoteEndpoint;
import eu.chessdata.backend.quoteEndpoint.model.Quote;


/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private SignInActivity mSignInActivity = this;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount mAcct;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]


    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            mAcct = result.getSignInAccount();
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

            String displayName = mAcct.getDisplayName();
            String idToken = mAcct.getIdToken();

            String charlesBuxton = getString(R.string.charlesBuxton);
            Log.d(TAG, "bogdan debug: displayName = " + displayName);
            mStatusTextView.setText(charlesBuxton + "\n" +
                    displayName + "\n" +
                    idToken);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_debug, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_test_endpoints:
                new EndpointsAsyncTask()
                        .execute(new Pair<Context, String>(this, "Bogdan to endpoint"));
                break;
            case R.id.action_create_quote:
                new CreateEndpointsAsyncTask()
                        .execute(new Pair<Context, String>(this, "Bogdan to endpoint"));
                break;
            case R.id.action_create_and_iterate_over_endpoints:
                new CreateAndStartIterate()
                        .execute(this);
                break;
            case R.id.action_getProfile:
                new GetProfile().execute(mAcct.getIdToken());
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
        private QuoteEndpoint quoteEndpoint = null;
        private Context context;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            if (quoteEndpoint == null) {  // Only do this once
                /*MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://192.168.0.12:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver*/


                QuoteEndpoint.Builder builder = new QuoteEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)

                        .setRootUrl("https://chess-data.appspot.com/_ah/api/");

                quoteEndpoint = builder.build();

            }

            context = params[0].first;
            String name = params[0].second;

           try {
                String idToken = mAcct.getIdToken();
               Quote quote = quoteEndpoint.getRandomQuote().setIdToken(idToken).execute();
               return quote.getWhat();
            } catch (IOException e) {
                return e.getMessage();
            }
            //return "no data";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }

    class CreateEndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, Quote> {
        private QuoteEndpoint quoteEndpoint = null;
        private Context context;

        @Override
        protected Quote doInBackground(Pair<Context, String>... params) {
            if (quoteEndpoint == null) {  // Only do this once

                QuoteEndpoint.Builder builder = new QuoteEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)

                        .setRootUrl("https://chess-data.appspot.com/_ah/api/");

                quoteEndpoint = builder.build();

            }

            context = params[0].first;
            String name = params[0].second;

            try {
                String idToken = mAcct.getIdToken();

                //Quote quote = quoteEndpoint.getRandomQuote().setIdToken(idToken).execute();
                Quote quote = quoteEndpoint.createNewQuote(idToken).execute();
                return quote;
            } catch (IOException e) {
                Quote quote = new Quote();
                quote.setWho("not valid");
                return quote;
            }
            //return "no data";
        }

        @Override
        protected void onPostExecute(Quote quote) {
            String result = quote.getWho()+"-"+quote.getWhat();
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
    }

    class IterateOverEndpoint extends AsyncTask<Pair<Context, Quote>, Quote, Quote> {
        private QuoteEndpoint quoteEndpoint = null;
        private Context context;

        @Override
        protected Quote doInBackground(Pair<Context, Quote>... params) {
            if (quoteEndpoint == null) {  // Only do this once
                QuoteEndpoint.Builder builder = new QuoteEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://chess-data.appspot.com/_ah/api/");
                quoteEndpoint = builder.build();
            }

            Log.d(TAG,"IterateOverEndpoint START");
            context = params[0].first;
            Quote quote = params[0].second;

            try {
                String idToken = mAcct.getIdToken();
                Quote updatedQuote = quoteEndpoint.findAndIncrementCount(quote).execute();
                return updatedQuote;
            } catch (IOException e) {
                Quote notValidQuote = new Quote();
                quote.setWho("not valid");
                return quote;
            }
            //return "no data";
        }

        @Override
        protected void onPostExecute(Quote quote) {
            Log.d(TAG,"IterateOverEndpoint START");
            if (quote.getWho()!="not valid"){
                String result ="("+quote+")";
                Long counter = quote.getCounter();
                Log.d(TAG, " IterateOverEndpoint post counter = "+counter +"("+result+")");
                if (counter < 5) {
                    Log.d(TAG, " IterateOverEndpoint connect again to endpoint = " + counter);
                    new IterateOverEndpoint()
                            .execute(new Pair<Context, Quote>(mSignInActivity,quote));
                }else{
                    Log.d(TAG, " THIS DATA IS > 5");
                }
            }else{
                Toast.makeText(context, "not valid", Toast.LENGTH_LONG).show();
            }
        }
    }

    class CreateAndStartIterate extends AsyncTask<Context, Quote, Quote> {
        private QuoteEndpoint quoteEndpoint = null;
        private Context context;

        @Override
        protected Quote doInBackground(Context ... params) {
            if (quoteEndpoint == null) {  // Only do this once
                QuoteEndpoint.Builder builder = new QuoteEndpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://chess-data.appspot.com/_ah/api/");
                quoteEndpoint = builder.build();
            }

            context = params[0];


            try {
                String idToken = mAcct.getIdToken();
                Quote newQuote = quoteEndpoint.createNewQuote(idToken).execute();
                return newQuote;
            } catch (IOException e) {
                Quote notValidQuote = new Quote();
                notValidQuote.setWho("not valid");
                return notValidQuote;
            }
            //return "no data";
        }

        @Override
        protected void onPostExecute(Quote quote) {
            Toast.makeText(context, "CreateAndStartIterate before"
                    +quote.getCounter()
                    +" "+quote.getWho(), Toast.LENGTH_LONG).show();
            if (quote.getWho()!="not valid"){
                String result ="("+quote+"):"+ quote.getWho()+"-"+quote.getWhat();
                if (quote.getCounter()< 5){
                    result = "Continue iterating: ";
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "DEBUG: CreateAndStartIterate => IterateOverEndpoint");
                    new IterateOverEndpoint()
                            .execute(new Pair<Context, Quote>(mSignInActivity, quote));
                }
            }else{
                Toast.makeText(context, "not valid", Toast.LENGTH_LONG).show();
            }
        }
    }

    class GetProfile extends AsyncTask<String,String,Profile>{

        @Override
        protected Profile doInBackground(String... params) {
            ProfileEndpoint.Builder builder =
                    new ProfileEndpoint.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            null)
                    .setRootUrl(MyGlobalSharedObjects.ROOT_URL);
            ProfileEndpoint profileEndpoint = builder.build();
            String idTokenString = params[0];
            Log.d(TAG, "GetProfile: param[0]="+idTokenString);
            try {
                Profile profile = profileEndpoint.getProfile(idTokenString).execute();
                Log.d(TAG,"GetProfile: "+profile.getName()+"-"+profile.getEmail()+"-"+profile.getDateCreated());
                return  profile;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}