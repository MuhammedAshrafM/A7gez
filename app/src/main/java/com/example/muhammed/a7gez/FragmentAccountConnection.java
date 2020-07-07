package com.example.muhammed.a7gez;

/**
 * Created by Mu7ammed_A4raf on 09-Jul-17.
 */

// interface used to do action when user click on button that exist in account fragment
public interface FragmentAccountConnection {

    public void setDataUser(String email, String fName, String lName, String imagePath, String phoneNumber);
    public void closeFragment();
    public void replaceFragment();
}
