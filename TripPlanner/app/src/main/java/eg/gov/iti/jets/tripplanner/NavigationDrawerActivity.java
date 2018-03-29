package eg.gov.iti.jets.tripplanner;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

import com.facebook.login.LoginManager;

import eg.gov.iti.jets.databasepkg.DBModel;
import eg.gov.iti.jets.historytripsfragment.HistoryPresenter;
import eg.gov.iti.jets.loginactivity.LoginActivity;
import eg.gov.iti.jets.picassopkg.ImageHelper;
import eg.gov.iti.jets.profilefragment.ProfileFragment;
import eg.gov.iti.jets.sharedprefernces.SharedPreferencesHelperClass;
import eg.gov.iti.jets.upcomingtripsfragment.UpcomingFragment;
import eg.gov.iti.jets.upcomingtripsfragment.UpcomingPresenter;
import eg.gov.iti.jets.dtos.User;
import eg.gov.iti.jets.historytripsfragment.HistoryFragment;


import java.io.Serializable;

public class NavigationDrawerActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener, Serializable {


    //private User user;
    private UpcomingFragment upcommingFragment;
    private UpcomingPresenter upcommingPresenter;
    private HistoryFragment historyFragment;
    private HistoryPresenter historyPresenter;
    private ProfileFragment profileFragment;
    private transient DrawerLayout drawer;
    private transient NavigationView navigationView;
    private transient TextView userName_navHeader;
    private transient TextView userEmail_navHeader;


    public static transient final String UPCOMMING_FRAGMENT_TAG = "upcomming_fragment";
    private static transient final String UPCOMMING_PRESENTER_TAG = "upcomming_presenter";
    public static transient final String HISTORY_FRAGMENT_TAG = "history_fragment";
    private static transient final String HISTORY_PRESENTER_TAG = "history_presenter";

    public static transient final String PROFILE_FRAGMENT_TAG = "profile_fragment";


    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBModel.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Log.i("GettenUser", "LoggedUser: " + User.getUser().getUserId());

        if (savedInstanceState == null && getSupportFragmentManager().findFragmentByTag(UPCOMMING_FRAGMENT_TAG) == null) {
            upcommingPresenter = new UpcomingPresenter(this);
            upcommingFragment = UpcomingFragment.newInstance(upcommingPresenter);
            upcommingPresenter.setView(upcommingFragment);
            getSupportFragmentManager().beginTransaction().
                    add(R.id.content_frame, upcommingFragment, UPCOMMING_FRAGMENT_TAG).commit();
            upcommingPresenter.getTripListFromDB();
            Log.i("wwwwwww", "onCreate: Up comming Created");
        } else {
            upcommingFragment = (UpcomingFragment) getSupportFragmentManager().findFragmentByTag(UPCOMMING_FRAGMENT_TAG);
            upcommingPresenter = (UpcomingPresenter) savedInstanceState.getSerializable(UPCOMMING_PRESENTER_TAG);
            if (savedInstanceState.getSerializable(HISTORY_PRESENTER_TAG) != null) {
                historyPresenter = (HistoryPresenter) savedInstanceState.getSerializable(HISTORY_PRESENTER_TAG);

            }
        }

        //code
        //drawer layout select item listener


        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //nav drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        user = User.getUser();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        navigationView = (NavigationView) NavigationDrawerActivity.this.findViewById(R.id.nav_view);
//        intialize navheader
        userName_navHeader = (TextView) navigationView.findViewById(R.id.userName_navHeader);
        if (userName_navHeader != null)
            userName_navHeader.setText(user.getUserName());
        userEmail_navHeader = (TextView) navigationView.findViewById(R.id.userEmail_navHeader);
        if (userEmail_navHeader != null)
            userEmail_navHeader.setText(user.getEmail());
        ImageView profilePic = (ImageView) navigationView.findViewById(R.id.profilePic);
        if (profilePic != null) {
            if (user.getPhoto() != null && !user.getPhoto().trim().equals("")) {
                ImageHelper.getImage(this, profilePic, user.getPhoto(), R.drawable.profile_default_photo);
            }

        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        // close drawer when item is tapped
        drawer.closeDrawers();
        // Create new fragment and transaction
        historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(HISTORY_FRAGMENT_TAG);
        upcommingFragment = (UpcomingFragment) getSupportFragmentManager().findFragmentByTag(UPCOMMING_FRAGMENT_TAG);

        profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);

        if (menuItem.getItemId() == R.id.upcommingItem) {
            navigationView.getMenu().findItem(R.id.pastItem).setChecked(false);
            navigationView.getMenu().findItem(R.id.profileItem).setChecked(false);
            if (upcommingFragment == null) {
                upcommingPresenter = new UpcomingPresenter(this);
                upcommingFragment = UpcomingFragment.newInstance(upcommingPresenter);
                upcommingPresenter.setView(upcommingFragment);
                upcommingPresenter.getTripListFromDB();
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.content_frame, upcommingFragment, UPCOMMING_FRAGMENT_TAG).commit();

                Log.i("wwwwwww", "onNavigationItemSelected: Upcomming Created ");

            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, upcommingFragment, UPCOMMING_FRAGMENT_TAG).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

                Log.i("wwwwwww", "onNavigationItemSelected: Upcomming Replaced ");

            }

        } else if (menuItem.getItemId() == R.id.pastItem) {
            navigationView.getMenu().findItem(R.id.upcommingItem).setChecked(false);
            navigationView.getMenu().findItem(R.id.profileItem).setChecked(false);

            if (historyFragment == null) {
                historyPresenter = new HistoryPresenter(this);
                historyFragment = HistoryFragment.newInstance(historyPresenter);
                historyPresenter.setView(historyFragment);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, historyFragment, HISTORY_FRAGMENT_TAG).addToBackStack(null).commit();
                historyPresenter.getTripListFromDB();
                Log.i("wwwwwww", "onNavigationItemSelected: History Created ");
            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, historyFragment, HISTORY_FRAGMENT_TAG).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

                Log.i("wwwwwww", "onNavigationItemSelected: History replaced ");
            }

        } else if (menuItem.getItemId() == R.id.profileItem) {

            navigationView.getMenu().findItem(R.id.upcommingItem).setChecked(false);
            navigationView.getMenu().findItem(R.id.pastItem).setChecked(false);
            if (profileFragment == null) {
                profileFragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().
                        add(R.id.content_frame, profileFragment, PROFILE_FRAGMENT_TAG).addToBackStack(null).commit();

                Log.i("wwwwwww", "onNavigationItemSelected: profile Created ");

            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, profileFragment, PROFILE_FRAGMENT_TAG).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

                Log.i("wwwwwww", "onNavigationItemSelected: Upcomming Replaced ");

            }

        } else if (menuItem.getItemId() == R.id.logoutItem) {
            if (SharedPreferencesHelperClass.isFacebookUser(this))
                LoginManager.getInstance().logOut();


            historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(HISTORY_FRAGMENT_TAG);
            upcommingFragment = (UpcomingFragment) getSupportFragmentManager().findFragmentByTag(UPCOMMING_FRAGMENT_TAG);
            profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);

            if (profileFragment != null) { getSupportFragmentManager().beginTransaction().remove(profileFragment).commit();}
            if (upcommingFragment != null) { getSupportFragmentManager().beginTransaction().remove(upcommingFragment).commit();}
            if (historyFragment != null) { getSupportFragmentManager().beginTransaction().remove(historyFragment).commit();}

            SharedPreferencesHelperClass.manageUser(this,
                    User.getUser(),
                    SharedPreferencesHelperClass.DELETE_USER,
                    null);
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(UPCOMMING_PRESENTER_TAG, upcommingPresenter);
        outState.putSerializable(HISTORY_PRESENTER_TAG, historyPresenter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }
}
