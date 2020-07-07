package com.example.muhammed.a7gez;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mu7ammed_A4raf on 14-Nov-18.
 */

public class Place implements Parcelable{

    private String id;
    private String name;
    private String type;
    private String imagePath;
    private String areaName;
    private String cityName;
    private GeoPoint location;
    private Double rate;
    private Long numberUsers;
    private ArrayList<String> cuisines;
    private ArrayList<String> beverages;
    private Map<String,Object> opening_hours;
    private ArrayList<String> paymentMethods;
    private ArrayList<Map<String,Object>> tables;
    private Double minimumCharge;
    private Double minimumDeposit;
    private ArrayList<Map<String,Object>> reviews;


    public Place(String id, String name, String type, String imagePath, String areaName, String cityName, GeoPoint location,
                 Double rate, Long numberUsers, ArrayList<String> cuisines, ArrayList<String> beverages,
                 Map<String,Object> opening_hours, ArrayList<String> paymentMethods, ArrayList<Map<String,Object>> tables,
                 Double minimumCharge, Double minimumDeposit, ArrayList<Map<String,Object>> reviews) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imagePath = imagePath;
        this.areaName = areaName;
        this.cityName = cityName;
        this.location = location;
        this.rate = rate;
        this.numberUsers = numberUsers;
        this.cuisines = cuisines;
        this.beverages = beverages;
        this.opening_hours = opening_hours;
        this.paymentMethods = paymentMethods;
        this.tables = tables;
        this.minimumCharge = minimumCharge;
        this.minimumDeposit = minimumDeposit;
        this.reviews = reviews;
    }

    public Place(Map<String, Object> map){
        this.id = map.get("id").toString();
        this.name = map.get("name").toString();
        this.type = map.get("type").toString();
        this.imagePath = map.get("imagePath").toString();
        this.areaName = map.get("areaName").toString();
        this.cityName = map.get("cityName").toString();
        this.location = (GeoPoint) map.get("location");
        this.rate = (Double) map.get("rate");
        this.numberUsers = (Long) map.get("numberUsers");
        this.cuisines = (ArrayList<String>) map.get("cuisines");
        this.beverages = (ArrayList<String>) map.get("beverages");
        this.opening_hours = (Map<String, Object>) map.get("opening_hours");
        this.paymentMethods = (ArrayList<String>) map.get("paymentMethod");
        this.tables = (ArrayList<Map<String,Object>>) map.get("table");
        this.minimumCharge = (Double) map.get("minimumCharge");
        this.minimumDeposit = (Double) map.get("minimumDeposit");
        this.reviews = (ArrayList<Map<String,Object>>) map.get("review");
    }

    protected Place(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readString();
        imagePath = in.readString();
        areaName = in.readString();
        cityName = in.readString();

        Double latitude = in.readDouble();
        Double longitude = in.readDouble();
        location = new GeoPoint(latitude,longitude);

        rate = in.readDouble();
        numberUsers = in.readLong();
        cuisines = in.readArrayList(String.class.getClassLoader());
        beverages = in.readArrayList(String.class.getClassLoader());

        opening_hours = new HashMap<String, Object>();
        in.readMap(opening_hours,Object.class.getClassLoader());

        paymentMethods = in.readArrayList(String.class.getClassLoader());
        tables = in.readArrayList(Map.class.getClassLoader());
        minimumCharge = in.readDouble();
        minimumDeposit = in.readDouble();
        reviews = in.readArrayList(Map.class.getClassLoader());
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(type);
        parcel.writeString(imagePath);
        parcel.writeString(areaName);
        parcel.writeString(cityName);
        parcel.writeDouble(location.getLatitude());
        parcel.writeDouble(location.getLongitude());
        parcel.writeDouble(rate);
        parcel.writeLong(numberUsers);
        parcel.writeList(cuisines);
        parcel.writeList(beverages);
        parcel.writeMap(opening_hours);
        parcel.writeList(paymentMethods);
        parcel.writeList(tables);
        parcel.writeDouble(minimumCharge);
        parcel.writeDouble(minimumDeposit);
        parcel.writeList(reviews);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Long getNumberUsers() {
        return numberUsers;
    }

    public void setNumberUsers(Long numberUsers) {
        this.numberUsers = numberUsers;
    }

    public ArrayList<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<String> cuisines) {
        this.cuisines = cuisines;
    }

    public ArrayList<String> getBeverages() {
        return beverages;
    }

    public void setBeverages(ArrayList<String> beverages) {
        this.beverages = beverages;
    }

    public Map<String, Object> getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(Map<String, Object> opening_hours) {
        this.opening_hours = opening_hours;
    }

    public ArrayList<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(ArrayList<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public ArrayList<Map<String,Object>> getTables() {
        return tables;
    }

    public void setTables(ArrayList<Map<String,Object>> tables) {
        this.tables = tables;
    }

    public Double getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(Double minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    public Double getMinimumDeposit() {
        return minimumDeposit;
    }

    public void setMinimumDeposit(Double minimumDeposit) {
        this.minimumDeposit = minimumDeposit;
    }

    public ArrayList<Map<String, Object>> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Map<String, Object>> reviews) {
        this.reviews = reviews;
    }
}
