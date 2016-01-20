package eu.chessdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "my-debug-tag";
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not enabled yet. Sorry! (speak with bogdan)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        if (savedInstanceState == null){
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

        //Set the user name
        String defaultValue = getString(R.string.pref_profile_signedOut);
        mSharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String displayName = mSharedPref.getString(
                getString(R.string.pref_profile_display_name), defaultValue);
        View header = navigationView.getHeaderView(0);
        ((TextView)header.findViewById(R.id.user_name)).setText(displayName);

        String email = mSharedPref.getString(
                getString(R.string.pref_profile_email), defaultValue);
        ((TextView)header.findViewById(R.id.user_email)).setText(email);

        //debug section
        String debugDefaultValue = "defaultValue";
        String debugDisplayName = mSharedPref.getString(
                getString(R.string.pref_profile_display_name), debugDefaultValue);
        String debugEmail = mSharedPref.getString(
                getString(R.string.pref_profile_email), debugDefaultValue);
        String debugProfileIdToken = mSharedPref.getString(
                getString(R.string.pref_profile_idToken), debugDefaultValue);
        Log.d(TAG, "[name,email,id]=" + debugDisplayName + "," + debugEmail + "," + debugProfileIdToken);
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
        }else if(id==R.id.action_create_club){
            (new ClubCreateDialogFragment()).show(getSupportFragmentManager(),"ClubCreateDialogFragment");
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
}
