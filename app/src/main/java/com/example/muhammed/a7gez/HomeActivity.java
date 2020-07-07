package com.example.muhammed.a7gez;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentLocationsConnection {

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
    private Preferences preferencesLocation, preferencesDataUser;
    private HashMap<String, String> hashMapPreferencesData;
    private String cityLocation, areaLocation, emailUser, firstNameUser, lastNameUser, imageProfileUserPath, creditCardUser;
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
    private LocationsFragment locationsFragment;
    private City city;
    private ArrayList<City> cityArrayList;
    private ArrayList<String> areasArrayList;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutNavigationHeader;
    private TextView textViewNameUser, textViewEmailUser;
    private CircleImageView circleImageViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        navMenu.getItem(0).setChecked(true);

        hView = navigationView.getHeaderView(0);
        linearLayoutNavigationHeader = (LinearLayout) hView.findViewById(R.id.linearLayout);
        textViewNameUser = (TextView) hView.findViewById(R.id.nameUser);
        textViewEmailUser = (TextView) hView.findViewById(R.id.emailUser);
        circleImageViewUser = (CircleImageView) hView.findViewById(R.id.imageUser);

        relativeLayout = (RelativeLayout) findViewById(R.id.main_content);
        editTextCity = (EditText) findViewById(R.id.city);
        editTextArea = (EditText) findViewById(R.id.area);
        buttonShowPlaces = (Button) findViewById(R.id.showPlaces);
        imageButtonCurrentLocation = (ImageButton) findViewById(R.id.currentLocation);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        editTextCity.setFocusable(false);
        editTextArea.setFocusable(false);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        snackbar = Snackbar.make(findViewById(R.id.main_content), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE);

        // Access a Cloud FireStore instance from your Activity
        firebaseFirestore = FirebaseFirestore.getInstance();

        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (view.getId()) {
                    case R.id.main_content:
                        if (snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        };

        relativeLayout.setOnTouchListener(onTouchListener);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.city:
                        // Checking for internet connection
                        if (!connectedInfo) {
                            Toast.makeText(HomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                            return;
                        }
                        showAvailableCities();
                        break;

                    case R.id.area:
                        showAvailableAreas();
                        break;

                    case R.id.currentLocation:
                        getCurrentLocation();
                        break;

                    case R.id.showPlaces:
                        showPlaces();
                        break;

                    case R.id.linearLayout:
                        startActivity(new Intent(HomeActivity.this,AccountActivity.class));
                        break;

                    default:
                        break;

                }
            }
        };

        editTextCity.setOnClickListener(onClickListener);
        editTextArea.setOnClickListener(onClickListener);
        buttonShowPlaces.setOnClickListener(onClickListener);
        imageButtonCurrentLocation.setOnClickListener(onClickListener);
        linearLayoutNavigationHeader.setOnClickListener(onClickListener);

        getPreferencesData();
    }

    private void getPreferencesData() {
        preferencesLocation = new Preferences(this, PREFERENCES_SPECIFIED_LOCATION);

        hashMapPreferencesData = new HashMap<>();
        hashMapPreferencesData = preferencesLocation.getSpecifiedLocation();
        cityLocation = hashMapPreferencesData.get(CITY_LOCATION).toString();
        areaLocation = hashMapPreferencesData.get(AREA_LOCATION).toString();

        preferencesDataUser = new Preferences(this, PREFERENCES_DATA_USER);
        isDataUserExist = preferencesDataUser.isDataUserExist();

        editTextCity.setText(cityLocation);
        editTextArea.setText(areaLocation);

        if (cityLocation.equals("") || areaLocation.equals("")) {
            navMenu.findItem(R.id.places).setVisible(false);
        } else {
            navMenu.findItem(R.id.places).setVisible(true);
        }

        if(isDataUserExist){
            hashMapPreferencesData = new HashMap<>();
            hashMapPreferencesData = preferencesDataUser.getDataUser();
            emailUser = hashMapPreferencesData.get(EMAIL_USER).toString();
            firstNameUser = hashMapPreferencesData.get(FIRST_NAME_USER).toString();
            lastNameUser = hashMapPreferencesData.get(LAST_NAME_USER).toString();
            imageProfileUserPath = hashMapPreferencesData.get(IMAGE_PROFILE_USER_PATH).toString();
            creditCardUser = hashMapPreferencesData.get(CREDIT_CARD_USER).toString();

            setDataNavigationHeader();
        }

        // Checking for internet connection
        checkConnection();
    }

    private void setPreferencesData(String cityLocation,String areaLocation){

        preferencesLocation.setSpecifiedLocation(cityLocation,areaLocation);
        preferencesLocation.setFirstTimeLaunch(false);

        editTextCity.setText(cityLocation);
        editTextArea.setText(areaLocation);
    }

    private void setDataNavigationHeader(){
        textViewNameUser.setText(firstNameUser + " " + lastNameUser);
        textViewEmailUser.setText(emailUser);
        CustomPicasso.loadImageUser(this, imageProfileUserPath, circleImageViewUser);
    }

    private void checkConnection() {

        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connectedInfo = snapshot.getValue(Boolean.class);
//                Toast.makeText(HomeActivity.this, ""+ connectedRef, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAvailableCities() {

        // Create and show fragment
        displayFragment(true, "City");

        // Read data from Cloud FireStore once
        firebaseFirestore.collection("cities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cityArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                city = new City(document.getData());
                                cityArrayList.add(city);
                            }

                            // pass all available city to the fragment
                            locationsFragment.setAvailableCities(cityArrayList);

                        } else {

                            Toast.makeText(HomeActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                            // Checking for internet connection
                            if (!connectedInfo) {
                                Toast.makeText(HomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }

    private void showAvailableAreas() {

        String cityLocation = preferencesLocation.getSpecifiedLocation().get(CITY_LOCATION).toString();


        if(!cityLocation.isEmpty()){

            // Create and show fragment
            displayFragment(true, "Area");

            // Read data from Cloud FireStore once
            firebaseFirestore.collection("cities")
                    .whereEqualTo("cityName", cityLocation)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                areasArrayList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    city = new City(document.getData());
                                    areasArrayList = city.getAreas();
                                }

                                // pass all available areas to the fragment
                                locationsFragment.setAvailableAreas(areasArrayList);


                            } else {

                                Toast.makeText(HomeActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                                // Checking for internet connection
                                if (!connectedInfo) {
                                    Toast.makeText(HomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

        }else {
            Toast.makeText(this, "Select city first", Toast.LENGTH_LONG).show();
        }

    }

    private void showPlaces() {
        if(preferencesLocation.getSpecifiedLocationCity().isEmpty()){
            Toast.makeText(this, "Select city first", Toast.LENGTH_LONG).show();
        }else {
            if(preferencesLocation.getSpecifiedLocationArea().isEmpty()){
                Toast.makeText(this, "Select area first", Toast.LENGTH_LONG).show();
            }else {
                launchPlacesScreen();
            }
        }
    }


    // get current location of the user
    private void getCurrentLocation() {
        // check if you have a permission
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            mayRequestLocation();
            return;
        }

        // Checking for internet connection
        if (!connectedInfo) {
            Toast.makeText(HomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }
        // get the last known location of a user's device
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    getMyLocation(location);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


    // save location data
    private void getMyLocation(Location location) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            cityLocation = addresses.get(0).getAdminArea();
            areaLocation = addresses.get(0).getLocality();

            checkAvailableArea(cityLocation, areaLocation);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkAvailableArea(final String cityLocation, final String areaLocation) {

        // Checking for internet connection
        if (!connectedInfo) {
            Toast.makeText(HomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore.collection("cities")
                .whereArrayContains("areas", areaLocation)
                .whereEqualTo("cityName", cityLocation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {

                                progressBar.setVisibility(View.GONE);
                                setPreferencesData(cityLocation, areaLocation);

                                launchPlacesScreen();
                            } else {
                                Toast.makeText(HomeActivity.this, "Obtaining your location", Toast.LENGTH_SHORT).show();
                                Toast.makeText(HomeActivity.this, "We are sorry! Your current location is not supported", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void mayRequestLocation() {
        /*
        If your app needs a dangerous permission, you must check whether you have that permission every time
        you perform an operation that requires that permission. Beginning with Android 6.0 (API level 23),
        users can revoke permissions from any app at any time, even if the app targets a lower API level.
        So even if the app used the camera yesterday, it can't assume it still has that permission today.
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {

            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                @TargetApi(Build.VERSION_CODES.M)
                public void onClick(View v) {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                }
            });

            snackbar.show();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
        return;
    }

    /**
     * Callback received when a permissions request has been completed.
     * to handle the case where the user grants the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mayRequestLocation();
                }
                break;

            default:
                break;
        }
    }

    private void launchPlacesScreen() {
        startActivity(new Intent(HomeActivity.this, PlacesActivity.class));
    }

    private void displayFragment(boolean show, String hint) {
        if (show) {

                locationsFragment = new LocationsFragment();
                locationsFragment.setHint(hint);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.main_content, locationsFragment, "Place Fragment")
                        .commit();
                getSupportActionBar().hide();
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(locationsFragment)
                    .commit();
            getSupportActionBar().show();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private void replaceFragment(ArrayList<String> areas, String hint) {

            locationsFragment = new LocationsFragment();
            locationsFragment.setHint(hint);
            locationsFragment.setAvailableAreas(areas);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, locationsFragment, "Place Fragment")
                    .commit();
            getSupportActionBar().hide();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if(locationsFragment.isVisible()){
                displayFragment(false, "");
                getPreferencesData();
            }
            else {
                super.onBackPressed();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.account_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            preferences.deleteSharedPreferencesData();
//            startActivity(new Intent(HomeActivity.this,WelcomeActivity.class));
//            finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.locationAddress) {
            // Handle the camera action
        } else if (id == R.id.places) {
            launchPlacesScreen();

        } else if (id == R.id.account) {
            startActivity(new Intent(HomeActivity.this,AccountActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void closeFragment() {
        displayFragment(false, "");
        getPreferencesData();
    }

    @Override
    public void replaceFragment(City city) {
        replaceFragment(city.getAreas(), "Area");
        setPreferencesData(city.getCityName(), "");
    }

    @Override
    public void selectArea(String areaLocation) {
        displayFragment(false, "");
        String cityLocation = preferencesLocation.getSpecifiedLocationCity();
        setPreferencesData(cityLocation, areaLocation);
        launchPlacesScreen();
    }
}
