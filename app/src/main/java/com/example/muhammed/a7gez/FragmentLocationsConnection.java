package com.example.muhammed.a7gez;

/**
 * Created by Mu7ammed_A4raf on 09-Jul-17.
 */

// interface used to do action when user click on button that exist in welcome screens
public interface FragmentLocationsConnection {

    public void closeFragment();
    public void replaceFragment(City city);
    public void selectArea(String areaLocation);
}
