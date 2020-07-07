package com.example.muhammed.a7gez;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlacesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


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
    private Preferences preferences;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu navMenu;
    private View hView;
    private View.OnClickListener onClickListener;
    private HashMap<String, String> hashMapPreferencesData;
    private String cityLocation, areaLocation, emailUser, firstNameUser, lastNameUser, imageProfileUserPath, creditCardUser;
    private boolean connectedInfo = false, isDataUserExist;
    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewPlaces;
    private RecyclerView.LayoutManager layoutManagerRecycler;
    private RecyclerViewPlacesAdapter placesAdapter;
    private TextView textViewNumberPlaces;
    private FirebaseFirestore firebaseFirestore;
    private Map<String, Object> objectMap;
    private Place place;
    private ArrayList<Place> placeArrayList;
    private LinearLayout linearLayoutNavigationHeader;
    private TextView textViewNameUser, textViewEmailUser;
    private CircleImageView circleImageViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

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
        navMenu.getItem(1).setChecked(true);

        hView = navigationView.getHeaderView(0);
        linearLayoutNavigationHeader = (LinearLayout) hView.findViewById(R.id.linearLayout);
        textViewNameUser = (TextView) hView.findViewById(R.id.nameUser);
        textViewEmailUser = (TextView) hView.findViewById(R.id.emailUser);
        circleImageViewUser = (CircleImageView) hView.findViewById(R.id.imageUser);


        textViewNumberPlaces = (TextView) findViewById(R.id.numberPlaces);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerViewPlaces = (RecyclerView) findViewById(R.id.places);
        layoutManagerRecycler = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPlaces.setLayoutManager(layoutManagerRecycler);

        // Access a Cloud FireStore instance from your Activity
        firebaseFirestore = FirebaseFirestore.getInstance();

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.linearLayout:
                        startActivity(new Intent(PlacesActivity.this,AccountActivity.class));
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

        getSupportActionBar().setTitle(areaLocation + " (" + cityLocation + ")");

        if(isDataUserExist){
            hashMapPreferencesData = new HashMap<>();
            hashMapPreferencesData = preferences.getDataUser();
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

    private void setDataNavigationHeader(){
        textViewNameUser.setText(firstNameUser + " " + lastNameUser);
        textViewEmailUser.setText(emailUser);
        CustomPicasso.loadImageUser(this, imageProfileUserPath, circleImageViewUser);
    }

    private void checkConnection() {

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connectedInfo = snapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PlacesActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLocations() {

        progressBar.setVisibility(View.VISIBLE);

        // Read data from Cloud FireStore once
        // get all popularly data for all places and all special data for the branch of each place by selected location
        firebaseFirestore.collection("places")
                .whereArrayContains("branches", areaLocation + "-" + cityLocation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        placeArrayList = new ArrayList<>();
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                objectMap = new HashMap<>();
                                objectMap.put("id", document.getId().toString());
                                objectMap.putAll(document.getData());

                                DocumentReference documentReference = document.getReference().collection("branches")
                                        .document(areaLocation + "-" + cityLocation);

                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            DocumentSnapshot document = task.getResult();
                                            objectMap.putAll(document.getData());

                                            place = new Place(objectMap);
                                            placeArrayList.add(place);


                                            progressBar.setVisibility(View.GONE);
                                            textViewNumberPlaces.setText(String.valueOf(placeArrayList.size()) + " places available for you");
                                            placesAdapter =new RecyclerViewPlacesAdapter(PlacesActivity.this, placeArrayList);
                                            recyclerViewPlaces.setAdapter(placesAdapter);
                                        }
                                    }
                                });
                            }


                        } else {

                            Toast.makeText(PlacesActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                            // Checking for internet connection
                            if (!connectedInfo) {
                                Toast.makeText(PlacesActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }


    private List<Place> filter (List<Place> pl, String query){

        while (query.startsWith(" ")){
            placesAdapter.setFilter(placeArrayList);
            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return placeArrayList;
            }
        }

        query = query.toLowerCase();
        final List<Place> filterModeList = new ArrayList<>();
        for(Place place: pl){
            final String placeName = place.getName().toLowerCase();
            final String placeType = place.getType().toLowerCase();

            if(placeName.contains(query) || placeType.contains(query) ||
                    filterBeverages(place, query) || filterCuisines(place, query)){
                filterModeList.add(place);
            }
        }
        return filterModeList;
    }

    private boolean filterBeverages(Place place, String query){
        for (int i = 0; i<place.getCuisines().size(); i++){
            if(place.getBeverages().get(i).toLowerCase().contains(query)){
                return true;
            }
        }
        return false;
    }

    private boolean filterCuisines(Place place, String query){
        for (int i = 0; i<place.getCuisines().size(); i++){
            if(place.getCuisines().get(i).toLowerCase().contains(query)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        showLocations();
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

        getMenuInflater().inflate(R.menu.places_menu, menu);
        final MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView) item.getActionView();

        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!placeArrayList.isEmpty()) {
                    searchView.setQuery(query, false);
                    final List<Place> filterModeList = filter(placeArrayList, query);
                    placesAdapter.setFilter(filterModeList);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!placeArrayList.isEmpty()) {
                    final List<Place> filterModeList = filter(placeArrayList, newText);
                    placesAdapter.setFilter(filterModeList);
                }
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filter:
                // open fragment or activity as you like contains on more than ways to get places
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
            startActivity(new Intent(PlacesActivity.this, HomeActivity.class));
        } else if (id == R.id.places) {

        } else if (id == R.id.account) {
            startActivity(new Intent(PlacesActivity.this,AccountActivity.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
