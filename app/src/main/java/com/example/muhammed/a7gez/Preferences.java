package com.example.muhammed.a7gez;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context _context;
 
    // mode type
    int PRIVATE_MODE = 0;

    private static final String IS_FIRST_TIME = "IsFirstTime";
    private static final String CITY_LOCATION = "CityLocation";
    private static final String AREA_LOCATION = "AreaLocation";
    private static final String IS_DATA_USER_EXIST = "IsDataUserExist";
    private static final String ID_USER = "idUser";
    private static final String EMAIL_USER = "EmailUser";
    private static final String FIRST_NAME_USER = "FirstNameUser";
    private static final String IMAGE_PROFILE_USER_PATH = "ImageProfileUserPath";
    private static final String LAST_NAME_USER = "LastNameUser";
    private static final String PHONE_NUMBER_USER = "PhoneNumberUser";
    private static final String CREDIT_CARD_USER = "CreditCardUser";

    // create Shared Preferences by a name and mode type
    public Preferences(Context context, String PREFERENCES_NAME) {
        this._context = context;
        preferences = _context.getSharedPreferences(PREFERENCES_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    // set true for Shared Preferences if user add specific location, and the opposite false
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME, isFirstTime);
        editor.commit();
    }

    // check to adding location or not
    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME, true);
    }

    // delete all data for this Shared Preferences
    public void deleteSharedPreferencesData() {
        editor.clear().commit();
    }

    // set location data Specified by the user
    public void setSpecifiedLocation(String cityLocation, String areaLocation) {
        editor.putString(CITY_LOCATION, cityLocation);
        editor.putString(AREA_LOCATION, areaLocation);
        editor.commit();
    }

    // get location data Specified by the user
    public HashMap<String,String> getSpecifiedLocation() {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(CITY_LOCATION, preferences.getString(CITY_LOCATION, ""));
        hashMap.put(AREA_LOCATION, preferences.getString(AREA_LOCATION, ""));
        return hashMap;
    }

    // get location city Specified by the user
    public String getSpecifiedLocationCity() {
        return preferences.getString(CITY_LOCATION, "");
    }


    // get location area Specified by the user
    public String getSpecifiedLocationArea() {
        return preferences.getString(AREA_LOCATION, "");
    }



    // set true for Shared Preferences if user add normal data, and the opposite false
    public void setDataUserExist(boolean isDataUserExist) {
        editor.putBoolean(IS_DATA_USER_EXIST, isDataUserExist);
        editor.commit();
    }

    // check to adding normal data or not
    public boolean isDataUserExist() {
        return preferences.getBoolean(IS_DATA_USER_EXIST, false);
    }

    // set normal data of the user
    public void setDataUser(String id, String email, String fName, String lName, String imagePath){
        editor.putString(ID_USER, id);
        editor.putString(EMAIL_USER, email);
        editor.putString(FIRST_NAME_USER, fName);
        editor.putString(LAST_NAME_USER, lName);
        editor.putString(IMAGE_PROFILE_USER_PATH, imagePath);
        editor.commit();
    }

    // set phone number user
    public void setPhoneNumber(String phoneNumber){
        editor.putString(PHONE_NUMBER_USER, phoneNumber);
        editor.commit();
    }

    // set credit card user
    public void setCreditCardUser(String creditCard){
        editor.putString(CREDIT_CARD_USER, creditCard);
        editor.commit();
    }

    // get normal data of the user
    public HashMap<String,String> getDataUser() {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put(ID_USER, preferences.getString(ID_USER, ""));
        hashMap.put(EMAIL_USER, preferences.getString(EMAIL_USER, ""));
        hashMap.put(FIRST_NAME_USER, preferences.getString(FIRST_NAME_USER, ""));
        hashMap.put(LAST_NAME_USER, preferences.getString(LAST_NAME_USER, ""));
        hashMap.put(IMAGE_PROFILE_USER_PATH, preferences.getString(IMAGE_PROFILE_USER_PATH, ""));
        hashMap.put(PHONE_NUMBER_USER, preferences.getString(PHONE_NUMBER_USER, ""));
        hashMap.put(CREDIT_CARD_USER, preferences.getString(CREDIT_CARD_USER, ""));
        return hashMap;
    }

}