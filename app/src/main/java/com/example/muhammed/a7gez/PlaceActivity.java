package com.example.muhammed.a7gez;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlaceActivity extends AppCompatActivity implements FragmentPlaceClosedConnection {

    private static final String PREFERENCES_SPECIFIED_LOCATION = "SPECIFIED_LOCATION";
    private static final String CITY_LOCATION = "CityLocation";
    private static final String AREA_LOCATION = "AreaLocation";
    private Preferences preferences;
    private HashMap<String, String> hashMapLocation;
    private String cityLocation, areaLocation;
    private RatingBar ratingBarPlaceRate;
    private TextView textViewPlaceName_type, textViewPlaceCuisines_beverages, textViewNumberUsers, textViewMinimumCharge,
            textViewMinimumDeposit;
    private ImageView imageViewPlaceImage;
    private Intent intent;
    private boolean connectedInfo = false;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewTable;
    private RecyclerView.LayoutManager layoutManagerRecycler;
    private RecyclerViewTableAdapter tableAdapter;
    private FirebaseFirestore firebaseFirestore;
    private String placeId, placeName, placeType, placeImagePath;
    private StringBuffer cuisinesStringBuffer, beveragesStringBuffer;
    private Long numberUsers;
    private Double placeRate, placeLatitude, placeLongitude, minimumCharge, minimumDeposit;
    private ArrayList<String> cuisinesArrayList, beveragesArrayList, paymentMethodsArrayList;
    private ArrayList<Map<String,Object>> tableArrayList;
    private Place place = null;
    private Map<String, Object> objectMap, opening_hours;
    private DocumentReference documentReferencePlace, documentReferenceBranch;
    private SearchView searchView;
    private DecimalFormat twoDecimalFormat;
    private PlaceClosedFragment placeClosedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // initiate all views
        initControls();
    }


    private void initControls() {

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ratingBarPlaceRate = (RatingBar) findViewById(R.id.placeRate);
        textViewPlaceName_type = (TextView) findViewById(R.id.placeName_type);
        textViewPlaceCuisines_beverages = (TextView) findViewById(R.id.placeCuisines_beverages);
        textViewNumberUsers = (TextView) findViewById(R.id.numberUsers);
        textViewMinimumCharge = (TextView) findViewById(R.id.minimumCharge);
        textViewMinimumDeposit = (TextView) findViewById(R.id.minimumDeposit);
        imageViewPlaceImage = (ImageView) findViewById(R.id.placeImage);
        recyclerViewTable = (RecyclerView) findViewById(R.id.tables);
        layoutManagerRecycler = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewTable.setLayoutManager(layoutManagerRecycler);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        twoDecimalFormat = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.getDefault()));

        // Access a Cloud FireStore instance from your Activity
        firebaseFirestore = FirebaseFirestore.getInstance();


        intent = getIntent();
        placeId = intent.getStringExtra("placeId").toString();

        getPreferencesData();

    }


    private void getPreferencesData() {
        preferences = new Preferences(this, PREFERENCES_SPECIFIED_LOCATION);

        hashMapLocation = new HashMap<>();
        hashMapLocation = preferences.getSpecifiedLocation();
        cityLocation = hashMapLocation.get(CITY_LOCATION).toString();
        areaLocation = hashMapLocation.get(AREA_LOCATION).toString();

        getSupportActionBar().setTitle(areaLocation + " (" + cityLocation + ")");

        // Checking for internet connection
        checkConnection();

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
                Toast.makeText(PlaceActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showDataLocationTransaction() {

        progressBar.setVisibility(View.VISIBLE);

        final DocumentReference documentReferencePlace = firebaseFirestore.collection("places").document(placeId);
        final DocumentReference documentReferenceBranch = documentReferencePlace.collection("branches")
                .document(areaLocation + "-" + cityLocation);

        firebaseFirestore.runTransaction(new Transaction.Function<Place>() {
            @Override
            public Place apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshotPlace = transaction.get(documentReferencePlace);
                DocumentSnapshot snapshotBranch = transaction.get(documentReferenceBranch);
                objectMap = new HashMap<>();
                objectMap.put("id", placeId);
                objectMap.putAll(snapshotPlace.getData());
                objectMap.putAll(snapshotBranch.getData());

                place = new Place(objectMap);

                return place;
            }
        }).addOnSuccessListener(new OnSuccessListener<Place>() {
            @Override
            public void onSuccess(Place place) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PlaceActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PlaceActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showDataLocation() {

        progressBar.setVisibility(View.VISIBLE);

        // Read data from Cloud FireStore once
        // get all popularly data for the place and all special data for the branch of the place
        documentReferencePlace = firebaseFirestore.collection("places").document(placeId);
        documentReferenceBranch = documentReferencePlace.collection("branches")
                .document(areaLocation + "-" + cityLocation);

        documentReferencePlace.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        objectMap = new HashMap<>();
                        if (task.isSuccessful()) {
                            objectMap.put("id", placeId);
                            objectMap.putAll(task.getResult().getData());

                            documentReferenceBranch.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                objectMap.putAll(task.getResult().getData());
                                                place = new Place(objectMap);

                                                setData(place);

                                            } else {

                                                Toast.makeText(PlaceActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                                                // Checking for internet connection
                                                if (!connectedInfo) {
                                                    Toast.makeText(PlaceActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });


                        } else {

                            Toast.makeText(PlaceActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();

                            // Checking for internet connection
                            if (!connectedInfo) {
                                Toast.makeText(PlaceActivity.this, "No internet connection", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void setData(Place place){
        placeName = place.getName();
        placeType = place.getType();
        placeImagePath = place.getImagePath();
        placeLatitude = place.getLocation().getLatitude();
        placeLongitude = place.getLocation().getLongitude();
        placeRate = Double.valueOf(twoDecimalFormat.format(place.getRate()));
        numberUsers = place.getNumberUsers();
        cuisinesArrayList = place.getCuisines();
        beveragesArrayList = place.getBeverages();
        opening_hours = place.getOpening_hours();
        tableArrayList = place.getTables();
        minimumCharge = Double.valueOf(twoDecimalFormat.format(place.getMinimumCharge()));
        minimumDeposit = Double.valueOf(twoDecimalFormat.format(place.getMinimumDeposit()));
        paymentMethodsArrayList = place.getPaymentMethods();

        cuisinesStringBuffer = new StringBuffer();
        beveragesStringBuffer = new StringBuffer();

        // check on work hours for a place
        if (getCurrentTimestamp(opening_hours)) {
            displayFragment();
        }

        getSupportActionBar().setTitle(placeName);
        ratingBarPlaceRate.setRating(Float.valueOf(String.valueOf(placeRate)));
        textViewNumberUsers.setText("(" + numberUsers + ")");
        textViewPlaceName_type.setText(placeName + " (" + placeType + ")");
        textViewMinimumCharge.setText(minimumCharge + " EGP");
        textViewMinimumDeposit.setText(minimumDeposit + " EGP");

        if (placeType.equals("Restaurant")) {
            for (String food : cuisinesArrayList) {
                cuisinesStringBuffer.append(food + ", ");
            }
            textViewPlaceCuisines_beverages.setText(cuisinesStringBuffer.substring(0, cuisinesStringBuffer.lastIndexOf(", ")));
        } else if (placeType.equals("Coffee")) {
            for (String drink : beveragesArrayList) {
                beveragesStringBuffer.append(drink + ", ");
            }
            textViewPlaceCuisines_beverages.setText(beveragesStringBuffer.substring(0, beveragesStringBuffer.lastIndexOf(", ")));
        }

        CustomPicasso.loadImage(PlaceActivity.this, placeImagePath, imageViewPlaceImage);

        tableAdapter =new RecyclerViewTableAdapter(PlaceActivity.this, tableArrayList);
        recyclerViewTable.setAdapter(tableAdapter);

    }

    private boolean getCurrentTimestamp(Map<String, Object> opening_hours) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        String currentTime = simpleTimeFormat.format(calendar.getTime());

        for (Object day : opening_hours.keySet()) {
            if (day.toString().contains(currentDate)) {
                try {
                    Date dateCurrent = simpleTimeFormat.parse(currentTime);
                    Date dateFrom = simpleTimeFormat.parse(opening_hours.get(day).toString().substring(0, 4));
                    Date dateTo = simpleTimeFormat.parse(opening_hours.get(day).toString().substring(8));

                    if ((dateCurrent.after(dateFrom) || dateCurrent.equals(dateFrom)) && (dateCurrent.before(dateTo) || dateCurrent.equals(dateTo))) {
                        return false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private List<Map<String,Object>> filter (List<Map<String,Object>> pl, String query){

        while (query.startsWith(" ")){
            tableAdapter.setFilter(tableArrayList);
            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return tableArrayList;
            }
        }

        query = query.toLowerCase();
        final List<Map<String,Object>> filterModeList = new ArrayList<>();
        for(Map<String,Object> table: pl){
            final boolean status = (boolean) table.get("available");
            final String tableStatus;
            if(status){
                tableStatus = "Available".toLowerCase();
            }else {
                tableStatus = "unavailable".toLowerCase();
            }
            final String seatsNumber = ("Seats: " + table.get("seatsNumber").toString()).toLowerCase();

            if(tableStatus.contains(query) || seatsNumber.contains(query)){
                filterModeList.add(table);
            }
        }
        return filterModeList;
    }

    private void displayFragment(){
        placeClosedFragment = new PlaceClosedFragment();
        placeClosedFragment.setPlaceData(placeName, placeType);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_content, placeClosedFragment, "Place Closed Fragment")
                .commit();
        getSupportActionBar().hide();
    }

    private void launchPlaceInfoScreen(){
        if(place != null) {
            Intent intent = new Intent(PlaceActivity.this, PlaceInfoActivity.class);
            intent.putExtra("place", place);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Please wait to get place info", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        showDataLocation();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.place_menu, menu);
        final MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView) item.getActionView();

        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!tableArrayList.isEmpty()) {
                    searchView.setQuery(query, false);
                    final List<Map<String,Object>> filterModeList = filter(tableArrayList, query);
                    tableAdapter.setFilter(filterModeList);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!tableArrayList.isEmpty()) {
                    final List<Map<String,Object>> filterModeList = filter(tableArrayList, newText);
                    tableAdapter.setFilter(filterModeList);
                }
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.info:
                launchPlaceInfoScreen();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openPlaceInfo() {
        launchPlaceInfoScreen();
        finish();
    }
}
