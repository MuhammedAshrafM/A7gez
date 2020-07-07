package com.example.muhammed.a7gez;

/**
 * Created by Mu7ammed_A4raf on 30-Oct-18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    int[] fragments;

    public SectionsPagerAdapter(FragmentManager fm, int[] fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a WelcomeFragment (defined as a static inner class below).
        return WelcomeFragment.newInstance(position, this.fragments);
    }

    // return fragments count
    @Override
    public int getCount() {
        return this.fragments.length;
    }
}
