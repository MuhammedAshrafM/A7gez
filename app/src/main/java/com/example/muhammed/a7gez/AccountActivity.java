package com.example.muhammed.a7gez;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentAccountConnection {

    private static final String PREFERENCES_SPECIFIED_LOCATION = "SPECIFIED_LOCATION";
    private static final String CITY_LOCATION = "CityLocation";
    private static final String AREA_LOCATION = "AreaLocation";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final String EMAIL_USER = "EmailUser";
    private static final String FIRST_NAME_USER = "FirstNameUser";
    private static final String LAST_NAME_USER = "LastNameUser";
    private static final String IMAGE_PROFILE_USER_PATH = "ImageProfileUserPath";
//    private static final String PASSWORD_USER = "PasswordUser";
    private static final String CREDIT_CARD_USER = "CreditCardUser";
    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
    private Preferences preferences;
    private HashMap<String, String> hashMapPreferencesData;
    private String cityLocation, areaLocation, emailUser, firstNameUser, lastNameUser, imageProfileUserPath, phoneNumberUser,
            creditCardUser;
    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private EditText editTextCity, editTextArea;
    private Button buttonShowPlaces;
    private ImageButton imageButtonCurrentLocation;
    private Menu navMenu;
    private View hView;
    private View.OnClickListener onClickListener;
    private View.OnTouchListener onTouchListener;
    private boolean connectedInfo = false, isDataUserExist;
    private FirebaseFirestore firebaseFirestore;
    private FusedLocationProviderClient mFusedLocationClient;
    private Snackbar snackbar;
    private Geocoder geocoder;
    private List<Address> addresses;
    private AccountFragment accountFragment;
    private City city;
    private ArrayList<City> cityArrayList;
    private ArrayList<String> areasArrayList;
    private LinearLayout linearLayoutNavigationHeader;
    private TextView textViewNameUser, textViewEmailUser;
    private CircleImageView circleImageViewUser;
    private Menu menuAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // initiate all views
        initControls();
    }

    private void initControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navMenu = navigationView.getMenu();
        navMenu.getItem(4).setChecked(true);

        hView = navigationView.getHeaderView(0);
        linearLayoutNavigationHeader = (LinearLayout) hView.findViewById(R.id.linearLayout);
        textViewNameUser = (TextView) hView.findViewById(R.id.nameUser);
        textViewEmailUser = (TextView) hView.findViewById(R.id.emailUser);
        circleImageViewUser = (CircleImageView) hView.findViewById(R.id.imageUser);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.linearLayout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    default:
                        break;
                }
            }
        };

        linearLayoutNavigationHeader.setOnClickListener(onClickListener);

        getPreferencesData();
    }

    private void getPreferencesData() {
        preferences = new Preferences(this, PREFERENCES_SPECIFIED_LOCATION);

        hashMapPreferencesData = new HashMap<>();
        hashMapPreferencesData = preferences.getSpecifiedLocation();
        cityLocation = hashMapPreferencesData.get(CITY_LOCATION).toString();
        areaLocation = hashMapPreferencesData.get(AREA_LOCATION).toString();

        preferences = new Preferences(this, PREFERENCES_DATA_USER);
        isDataUserExist = preferences.isDataUserExist();


        if (cityLocation.equals("") || areaLocation.equals("")) {
            navMenu.findItem(R.id.places).setVisible(false);
        } else {
            navMenu.findItem(R.id.places).setVisible(true);
        }

        if(!isDataUserExist){
            displayFragment(true);
            setDataNavigationHeader("Log in to your account", "", "", "");
//            menuItem.setVisible(false);
        }else {
            hashMapPreferencesData = new HashMap<>();
            hashMapPreferencesData = preferences.getDataUser();
            emailUser = hashMapPreferencesData.get(EMAIL_USER).toString();
            firstNameUser = hashMapPreferencesData.get(FIRST_NAME_USER).toString();
            lastNameUser = hashMapPreferencesData.get(LAST_NAME_USER).toString();
            imageProfileUserPath = hashMapPreferencesData.get(IMAGE_PROFILE_USER_PATH).toString();
            creditCardUser = hashMapPreferencesData.get(CREDIT_CARD_USER).toString();

            // add all user data in account activity
            setDataNavigationHeader(emailUser, firstNameUser, lastNameUser, imageProfileUserPath);

        }
        // Checking for internet connection
//        checkConnection();
    }

    private void displayFragment(boolean show){
        if (show) {
            accountFragment = new AccountFragment();
            accountFragment.setActivityData(AccountActivity.this, "");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_content, accountFragment, "Account Fragment")
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(accountFragment)
                    .commit();
        }

//        getSupportActionBar().hide();
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void setDataNavigationHeader(String email, String fName, String lName, String imagePath){
        textViewNameUser.setText(fName + " " + lName);
        textViewEmailUser.setText(email);
        if(imagePath.length() != 0){
            circleImageViewUser.setVisibility(View.VISIBLE);
            CustomPicasso.loadImageUser(this, imagePath, circleImageViewUser);
        }else {
            circleImageViewUser.setVisibility(View.INVISIBLE);
        }

    }

    private void logOut(){
        menuAccount.setGroupVisible(0, false);
        preferences = new Preferences(this, PREFERENCES_DATA_USER);
        preferences.setDataUserExist(false);
        getPreferencesData();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menuAccount = menu;
        getMenuInflater().inflate(R.menu.account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logOut:
                logOut();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.locationAddress) {
            startActivity(new Intent(AccountActivity.this,HomeActivity.class));
        } else if (id == R.id.places) {
            startActivity(new Intent(AccountActivity.this, PlacesActivity.class));
        } else if (id == R.id.account) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setDataUser(String email, String fName, String lName, String imagePath, String phoneNumber) {
        emailUser = email;
        firstNameUser = fName;
        lastNameUser = lName;
        imageProfileUserPath = imagePath;
        phoneNumberUser = phoneNumber;

        if(phoneNumber != null){
            // remove this fragment and display account activity that contains on user data
            displayFragment(false);
            // setUserInfo();
        }

        setDataNavigationHeader(emailUser, firstNameUser, lastNameUser, imageProfileUserPath);
    }

    @Override
    public void closeFragment() {
        // remove this fragment and display account activity that contains on user data after adding phone number user
        displayFragment(false);
        // setUserInfo();
    }

    @Override
    public void replaceFragment() {

    }

}
