package com.example.muhammed.a7gez;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class PlaceInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String cityLocation;
    private RatingBar ratingBarPlaceRate, ratingBarPlaceRate2;
    private TextView textViewPlaceName_type, textViewPlaceCuisines_beverages, textViewNumberUsers, textViewMinimumCharge,
            textViewMinimumDeposit, textViewNumberRatings, textViewPlaceRate, textViewCityName;
    private ImageView imageViewPlaceImage;
    private Intent intent;
    private RecyclerView recyclerViewOpeningHours, recyclerViewPaymentMethods, recyclerViewReviews;
    private RecyclerView.LayoutManager layoutManagerRecycler;
    private RecyclerViewPlaceInfoAdapter placeInfoAdapter;
    private RecyclerViewPlaceReviewsAdapter placeReviewsAdapter;
    private String placeName, placeType, placeImagePath;
    private StringBuffer cuisinesStringBuffer, beveragesStringBuffer;
    private Long numberUsers;
    private Double placeRate, placeLatitude, placeLongitude, minimumCharge, minimumDeposit;
    private ArrayList<String> cuisinesArrayList, beveragesArrayList, paymentMethodsArrayList;
    private ArrayList<Map<String,Object>> tableArrayList;
    private ArrayList<Map<String,Object>> reviewsArrayList;
    private Place place;
    private Map<String, Object> opening_hours;
    private DecimalFormat twoDecimalFormat;

    private GoogleMap mMap = null;
    private ScrollView mScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        // initiate all views
        initControls();

    }


    private void initControls() {

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScrollView = findViewById(R.id.scrollView);
        ratingBarPlaceRate = (RatingBar) findViewById(R.id.placeRate);
        ratingBarPlaceRate2 = (RatingBar) findViewById(R.id.placeRateBar);
        textViewPlaceName_type = (TextView) findViewById(R.id.placeName_type);
        textViewPlaceCuisines_beverages = (TextView) findViewById(R.id.placeCuisines_beverages);
        textViewNumberUsers = (TextView) findViewById(R.id.numberUsers);
        textViewMinimumCharge = (TextView) findViewById(R.id.minimumCharge);
        textViewMinimumDeposit = (TextView) findViewById(R.id.minimumDeposit);
        textViewNumberRatings = (TextView) findViewById(R.id.numberRatings);
        textViewPlaceRate = (TextView) findViewById(R.id.placeRateText);
        textViewCityName = (TextView) findViewById(R.id.cityName);
        imageViewPlaceImage = (ImageView) findViewById(R.id.placeImage);
        recyclerViewOpeningHours = (RecyclerView) findViewById(R.id.openingHours);
        recyclerViewPaymentMethods = (RecyclerView) findViewById(R.id.paymentMethods);
        recyclerViewReviews = (RecyclerView) findViewById(R.id.reviews);

        layoutManagerRecycler = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewOpeningHours.setLayoutManager(layoutManagerRecycler);
        layoutManagerRecycler = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPaymentMethods.setLayoutManager(layoutManagerRecycler);
        layoutManagerRecycler = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewReviews.setLayoutManager(layoutManagerRecycler);

        twoDecimalFormat = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.getDefault()));

        setGoogleMaps();

        intent = getIntent();
        place = intent.getParcelableExtra("place");

        setData(place);
    }

    private void setGoogleMaps(){

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // check if we have got the googleMap already
        if (mMap == null) {
            // custom MapFragment to disallow touch on scrollview when touch on google map
            SupportMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
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
        cityLocation = place.getCityName();
        reviewsArrayList = place.getReviews();
        placeRate = Double.valueOf(twoDecimalFormat.format(placeRate));

        cuisinesStringBuffer = new StringBuffer();
        beveragesStringBuffer = new StringBuffer();

//        getSupportActionBar().setTitle("Info");
        ratingBarPlaceRate.setRating(Float.valueOf(String.valueOf(placeRate)));
        ratingBarPlaceRate2.setRating(Float.valueOf(String.valueOf(placeRate)));
        textViewPlaceRate.setText(String.valueOf(placeRate));
        textViewNumberUsers.setText("(" + numberUsers + ")");
        textViewNumberRatings.setText(numberUsers + " Ratings");
        textViewPlaceName_type.setText(placeName + " (" + placeType + ")");
        textViewMinimumCharge.setText(minimumCharge + " EGP");
        textViewMinimumDeposit.setText(minimumDeposit + " EGP");
        textViewCityName.setText(cityLocation);


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

        CustomPicasso.loadImage(PlaceInfoActivity.this, placeImagePath, imageViewPlaceImage);

        setPlaceInfo();
    }

    private void setPlaceInfo(){

        setWorkHoursPlace();

        placeInfoAdapter =new RecyclerViewPlaceInfoAdapter(paymentMethodsArrayList);
        recyclerViewPaymentMethods.setAdapter(placeInfoAdapter);

        placeReviewsAdapter =new RecyclerViewPlaceReviewsAdapter(reviewsArrayList);
        recyclerViewReviews.setAdapter(placeReviewsAdapter);
    }

    private void setWorkHoursPlace(){
        String[] days_of_week =  getResources().getStringArray(R.array.days_of_week);
        StringBuffer stringBuffer;
        ArrayList<String> daysArrayList = new ArrayList<>();
        ArrayList<StringBuffer> openingHoursArrayList = new ArrayList<>();

        for(int j = 0; j< days_of_week.length; j++){

            stringBuffer = new StringBuffer();
            for (Object day : opening_hours.keySet()) {
                if (day.toString().contains(days_of_week[j])) {
                    stringBuffer.append(opening_hours.get(day) + "\n");
                }
            }

            if(stringBuffer.length() != 0){
                daysArrayList.add(days_of_week[j]);
                openingHoursArrayList.add(stringBuffer);
            }
        }

        placeInfoAdapter =new RecyclerViewPlaceInfoAdapter(daysArrayList, openingHoursArrayList);
        recyclerViewOpeningHours.setAdapter(placeInfoAdapter);

    }

    private void addMarkerInMap(Double placeLatitude, Double placeLongitude, String placeName){

        // create and combine two bitmaps(place image and arrow icon) in bitmap
        Bitmap bitmap = createBitmaps();

        // Add a marker in Sydney and move the camera
        if(mMap !=null && bitmap != null){
            LatLng sydney = new LatLng(placeLatitude, placeLongitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title(placeName))
            .setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
        }
    }

    private Bitmap createBitmaps(){
        Bitmap bitmapPlace = null;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.arrow_down_float);

        try {
            bitmapPlace = ((BitmapDrawable)imageViewPlaceImage.getDrawable()).getBitmap();
        } catch (Exception e) {
            bitmapPlace = null;
        }

        if(bitmapPlace != null) {
            bitmapPlace = Bitmap.createScaledBitmap(bitmapPlace, (int) (bitmapPlace.getWidth() * 0.5), (int) (bitmapPlace.getHeight() * 0.5), true);

            return combineImages(bitmapPlace, bitmap);
        }
        else {
            // add default image for a place that not contains on image or in case not internet connection
            Bitmap bitmapPlaceDefault = BitmapFactory.decodeResource(getResources(), R.drawable.default_image_place);
            bitmapPlaceDefault = Bitmap.createScaledBitmap(bitmapPlaceDefault, (int) ((bitmapPlaceDefault.getWidth() * 0.1)), (int) (bitmapPlaceDefault.getWidth() * 0.1), true);

            return combineImages(bitmapPlaceDefault, bitmap);
        }

    }
    private Bitmap combineImages(Bitmap bitmap, Bitmap bitmap2) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap bitmapCombine = null;

        int width, height = 0;

        if(bitmap.getWidth() > bitmap2.getWidth()) {
            width = bitmap.getWidth();
            height = bitmap.getHeight() + bitmap2.getHeight();
        } else {
            width = bitmap2.getWidth();
            height = bitmap.getHeight() + bitmap2.getHeight();
        }

        bitmapCombine = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(bitmapCombine);

        comboImage.drawBitmap(bitmap, 0f, 0f, null);
        comboImage.drawBitmap(bitmap2, bitmap.getWidth()/2 - bitmap2.getWidth()/2, bitmap.getHeight(), null);

        return bitmapCombine;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMap.getUiSettings().setZoomControlsEnabled(true);

        // add place image in it location
        addMarkerInMap(placeLatitude, placeLongitude, placeName);

        // disallow touch on scrollview when touch on google map
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch()
                    {
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });

    }
}
