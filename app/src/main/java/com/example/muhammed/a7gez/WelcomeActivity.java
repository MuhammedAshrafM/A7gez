package com.example.muhammed.a7gez;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WelcomeActivity extends AppCompatActivity implements FragmentWelcomeConnection {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager viewPager, viewPagerBeginning;
    private LinearLayout linearLayoutDots;
    private RelativeLayout relativeLayout;
    private TextView[] textViewsDots;
    private int[] layouts;
    private Preferences preferences;
    private static final String PREFERENCES_SPECIFIED_LOCATION = "SPECIFIED_LOCATION";
    private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String cityLocation, areaLocation;
    private Snackbar snackbar;
    private View.OnTouchListener onTouchListener;
    private AlertDialog alertDialog = null;
    private View view;
    private boolean connectedInfo = false;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get all cities and regions stored in cloud FireStore and will be stored in the cache to be used again
        getAvailableCities();
    }

    private void checkConnection(){

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connectedInfo = snapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(WelcomeActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayFragment(boolean show){
        if(show) {
            viewPagerBeginning = (ViewPager) findViewById(R.id.containerBeginning);
            viewPagerBeginning.setVisibility(View.VISIBLE);
            layouts = new int[]{R.layout.fragment_beginning};
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), layouts);
            viewPagerBeginning.setAdapter(mSectionsPagerAdapter);
        }else {
            viewPagerBeginning.setVisibility(View.GONE);
        }
    }

    private void getAvailableCities(){
        // Checking for internet connection
        checkConnection();

        // Create and show fragment to display at the beginning
        displayFragment(true);

        // Access a Cloud FireStore instance from your Activity
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Read data from Cloud FireStore once
        firebaseFirestore.collection("cities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Map> arrayListDocument = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arrayListDocument.add(document.getData());
                    }
                    if(!connectedInfo){
                        Toast.makeText(WelcomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                    }
//                    Toast.makeText(WelcomeActivity.this, arrayListDocument.size()+"", Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(WelcomeActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                    // Checking for internet connection
                    if(!connectedInfo){
                        Toast.makeText(WelcomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }

                // Checking for first time launch
                preferences = new Preferences(WelcomeActivity.this, PREFERENCES_SPECIFIED_LOCATION);
                if (!preferences.isFirstTimeLaunch()) {
                    preferences.setFirstTimeLaunch(false);
                    launchHomeScreen();
                    return;
                }

                // initiate all views
                initControls();
            }
        });
    }

    private void initControls() {
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setVisibility(View.VISIBLE);
        linearLayoutDots = (LinearLayout) findViewById(R.id.linearLayoutDots);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        snackbar = Snackbar.make(findViewById(R.id.container), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE);

        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (view.getId()){
                    case R.id.container:
                        if(snackbar.isShown()){
                            snackbar.dismiss();
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        };

        viewPager.setOnTouchListener(onTouchListener);

        initViewPagerControls();
    }

    private void initViewPagerControls() {
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_screen1,
                R.layout.welcome_screen2};

        // adding bottom dots
        addBottomDots(0);

        // Set up the ViewPager with the sections adapter.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),layouts);
        viewPager.setAdapter(mSectionsPagerAdapter);

        // add listener method for view pager
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    @SuppressLint("ResourceAsColor")
    private void addBottomDots(int currentPage) {
        textViewsDots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_pager_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_pager_inactive);
        // remove all views that exist in linear layout view to adding new text views
        linearLayoutDots.removeAllViews();
        for (int i = 0; i < textViewsDots.length; i++) {
            textViewsDots[i] = new TextView(this);
            textViewsDots[i].setText(Html.fromHtml("•"));
            textViewsDots[i].setTextSize(35);
            textViewsDots[i].setTextColor(colorsInactive[currentPage]);
            linearLayoutDots.addView(textViewsDots[i]);
        }
        if (textViewsDots.length > 0)
            textViewsDots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void launchHomeScreen() {
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        finish();
    }

    // viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Toast.makeText(WelcomeActivity.this, "scroll", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
            Toast.makeText(WelcomeActivity.this, "state", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void moveScreen(int currentPosition) {
        viewPager.setCurrentItem(currentPosition);
    }

    @Override
    public void getLocationData() {
        getCurrentLocation();
    }

    @Override
    public void launchHomeActivity() {
        preferences.setFirstTimeLaunch(true);
        launchHomeScreen();
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
        if(!connectedInfo){
            Toast.makeText(WelcomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
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
    private void getMyLocation(Location location){
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            cityLocation = addresses.get(0).getAdminArea();
            areaLocation = addresses.get(0).getLocality();

            checkAvailableArea(cityLocation,areaLocation);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkAvailableArea(final String cityLocation, final String areaLocation){

        // Checking for internet connection
        if(!connectedInfo){
            Toast.makeText(WelcomeActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
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
                    if(!task.getResult().isEmpty()){
                        progressBar.setVisibility(View.GONE);
                        preferences.setSpecifiedLocation(cityLocation,areaLocation);
                        preferences.setFirstTimeLaunch(false);
                        launchHomeScreen();
                    }else {
                        Toast.makeText(WelcomeActivity.this, "Obtaining your location", Toast.LENGTH_SHORT).show();
                        Toast.makeText(WelcomeActivity.this, "We are sorry! Your current location is not supported", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(WelcomeActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
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

        switch (requestCode){
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mayRequestLocation();
                    getCurrentLocation();
                }
                break;

            default:
                break;
        }
    }
}
