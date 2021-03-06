package eu.chessdata;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import eu.chessdata.data.simplesql.ClubMemberTable;
import eu.chessdata.data.simplesql.ClubTable;
import eu.chessdata.members.MainMembersFragment;
import eu.chessdata.round.RoundPagerFragment;
import eu.chessdata.services.ProfileService;
import eu.chessdata.services.LocalService;
import eu.chessdata.tools.MyGlobalTools;
import eu.chessdata.tournament.TournamentPlayersFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TournamentFragment.TournamentCallback,
        TournamentDetailsFragment.TournamentDetailsCallback,
        MainMembersFragment.MainMembersCallback {
    private String TAG = "my-debug-tag";
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            QuoteFragment firstFragment = new QuoteFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //update all members map
        ProfileService.startActionUpdateAllMembersMap(getBaseContext());

        //Set the user name
        String defaultValue = getString(R.string.pref_profile_signedOut);
        mSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String displayName = mSharedPref.getString(
                getString(R.string.pref_profile_display_name), defaultValue);
        View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.user_name)).setText(displayName);

        String email = mSharedPref.getString(
                getString(R.string.pref_profile_email), defaultValue);
        ((TextView) header.findViewById(R.id.user_email)).setText(email);

        //debug section
        String debugDefaultValue = "defaultValue";
        String debugDisplayName = mSharedPref.getString(
                getString(R.string.pref_profile_display_name), debugDefaultValue);
        String debugEmail = mSharedPref.getString(
                getString(R.string.pref_profile_email), debugDefaultValue);
        String debugProfileIdToken = mSharedPref.getString(
                getString(R.string.pref_profile_idToken), debugDefaultValue);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_managed_club) {
            //(new DeviceSetDefaultManagedClub()).show(getSupportFragmentManager(),"DeviceSetDefaultManagedClub");
            String debugDefaultValue = "defaultValue";
            String profileId = mSharedPref.getString(
                    getString(R.string.pref_profile_profileId), debugDefaultValue);
            ContentResolver contentResolver = getContentResolver();
            ManagedClub managedClub = new ManagedClub(getSupportFragmentManager(), profileId, contentResolver);
            managedClub.execute();
        } else if (id == R.id.action_create_club) {
            (new ClubCreateDialogFragment()).show(getSupportFragmentManager(), "ClubCreateDialogFragment");
        } else if (id == R.id.action_sync_data_cloud_to_device) {
            LocalService.startActionSynchronizeAll(getApplicationContext());
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tournament) {
            // Handle the camera action
            TournamentFragment tournamentFragment = new TournamentFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, tournamentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_members) {
            MainMembersFragment mainMembersFragment = new MainMembersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, mainMembersFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_gallery) {
            QuoteFragment quoteFragment = new QuoteFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, quoteFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class ManagedClub extends AsyncTask<Void, Void, Void> {

        private FragmentManager mFragmentManager;
        private String mProfileId;
        ContentResolver mContentResolver;

        public ManagedClub(FragmentManager fragmentManager, String profileId,
                           ContentResolver contentResolver) {
            mFragmentManager = fragmentManager;
            mProfileId = profileId;
            mContentResolver = contentResolver;
        }

        @Override
        protected Void doInBackground(Void... nothing) {
            Map<String, Long> map = new HashMap<>();

            //select members for current user and also club managers
            String selectionClause = ClubMemberTable.FIELD_PROFILEID + " = ? "
                    + "AND " + ClubMemberTable.FIELD_MANAGERPROFILE + " = ?";
            String[] selectionArgs = {mProfileId, "1"};
            Cursor memberCursor = mContentResolver.query(
                    ClubMemberTable.CONTENT_URI,
                    null,
                    selectionClause,
                    selectionArgs,
                    null
            );

            Map<Long, String> clubIdToMember = new HashMap<>();
            int memberCount = memberCursor.getCount();
            int idx_clubId = memberCursor.getColumnIndex(ClubMemberTable.FIELD_CLUBID);
            int idx_profileId = memberCursor.getColumnIndex(ClubMemberTable.FIELD_PROFILEID);
            int idx_managerProfile = memberCursor.getColumnIndex(ClubMemberTable.FIELD_MANAGERPROFILE);
            while (memberCursor.moveToNext()) {
                long clubId = memberCursor.getLong(idx_clubId);
                String profileId = memberCursor.getString(idx_profileId);
                int manager = memberCursor.getInt(idx_managerProfile);
                clubIdToMember.put(clubId, profileId + "/" + manager);

                //get clubSqlId and club name
                String clubClause = ClubTable.FIELD_CLUBID + " = ?";
                String[] clubArguments = {Long.toString(clubId)};
                Cursor clubCursor = mContentResolver.query(
                        ClubTable.CONTENT_URI,
                        null,
                        clubClause,
                        clubArguments,
                        null
                );
                int idx_clubSqlId = clubCursor.getColumnIndex(ClubTable.FIELD__ID);
                int idx_clubName = clubCursor.getColumnIndex(ClubTable.FIELD_NAME);
                while (clubCursor.moveToNext()) {
                    long clubSqlId = clubCursor.getLong(idx_clubSqlId);
                    String clubName = clubCursor.getString(idx_clubName);
                    map.put(clubName, clubSqlId);
                }
                clubCursor.close();
            }
            memberCursor.close();

            MyGlobalTools.managedClubs = map;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            DeviceSetDefaultManagedClub dialog = new DeviceSetDefaultManagedClub();
            dialog.show(mFragmentManager, "somTag");
        }
    }

    public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
        private final String LINE_SEPARATOR = "\n";

        @SuppressWarnings("deprecation")
        public void uncaughtException(Thread thread, Throwable exception) {
            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));

            StringBuilder errorReport = new StringBuilder();
            errorReport.append(stackTrace.toString());

            Log.e(TAG, errorReport.toString());

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }


    @Override
    public void onTournamentItemSelected(Uri tournamentUri, String tournamentName) {

        //prepare the data
        String uriString = tournamentUri.toString();
        Bundle bundle = new Bundle();
        bundle.putString(TournamentDetailsFragment.TOURNAMENT_URI, uriString);
        bundle.putString(TournamentDetailsFragment.TOURNAMENT_NAME, tournamentName);

        TournamentDetailsFragment fragment = new TournamentDetailsFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onTournamentDetailsItemSelected(int selection, String stringUri, String name) {
        String uriString = stringUri;
        Bundle bundle = new Bundle();
        bundle.putString(TournamentDetailsFragment.TOURNAMENT_URI, uriString);
        bundle.putString(TournamentDetailsFragment.TOURNAMENT_NAME, name);
        switch (selection) {
            case TournamentDetailsFragment.PLAYERS:

                TournamentPlayersFragment fragment = new TournamentPlayersFragment();
                fragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case TournamentDetailsFragment.ROUNDS:

                RoundPagerFragment roundPagerFragment = new RoundPagerFragment();
                roundPagerFragment.setArguments(bundle);
                FragmentTransaction transactionPager = getSupportFragmentManager().beginTransaction();
                transactionPager.replace(R.id.fragment_container, roundPagerFragment);
                transactionPager.addToBackStack(null);
                transactionPager.commit();

                break;
        }
    }

    @Override
    public void onMainMembersCallback(Uri memberUri) {
        Log.d(TAG, "Home activity: Time to replace fragments: " + memberUri);
    }
}
