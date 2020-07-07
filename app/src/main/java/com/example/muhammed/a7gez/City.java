package com.example.muhammed.a7gez;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mu7ammed_A4raf on 02-Nov-18.
 */

public class City {

    private String cityId;
    private String cityName;
    private ArrayList<String> areas;

    public City() {
    }


    public City(String cityId, String cityName, ArrayList<String> areas) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.areas = areas;
    }

    public City(Map<String, Object> map){
        this.cityId = map.get("cityId").toString();
        this.cityName = map.get("cityName").toString();
        this.areas = (ArrayList<String>) map.get("areas");
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cityId", getCityId());
        result.put("cityName", getCityName());
        result.put("areas", getAreas());
        return result;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public ArrayList<String> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<String> areas) {
        this.areas = areas;
    }
}
