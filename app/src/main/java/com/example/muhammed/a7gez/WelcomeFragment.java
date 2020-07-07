package com.example.muhammed.a7gez;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Mu7ammed_A4raf on 30-Oct-18.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class WelcomeFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private Button buttonLeft, buttonRight;
    private int[] layouts;
    private int positionFragment;
    private View.OnClickListener onClickListener;
    private FragmentWelcomeConnection connection;
    private int specifiedPosition;

    public WelcomeFragment() {
    }

    @SuppressLint("ValidFragment")
    public WelcomeFragment(int[] layouts) {
        this.layouts = layouts;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WelcomeFragment newInstance(int sectionNumber, int[] layouts) {
        WelcomeFragment fragment = new WelcomeFragment(layouts);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        positionFragment = getArguments().getInt(ARG_SECTION_NUMBER);
        rootView = inflater.inflate(layouts[positionFragment], container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(layouts[0] == R.layout.welcome_screen1){
            initControls();
        }

    }

    private void initControls() {
        buttonLeft = (Button) rootView.findViewById(R.id.buttonLeft);
        buttonRight = (Button) rootView.findViewById(R.id.buttonRight);

        connection = (FragmentWelcomeConnection)getActivity();

        // changing the next button text 'NEXT' / 'GOT IT'
        if (positionFragment == layouts.length - 1) {
            // last page. make button text to GOT IT
            buttonLeft.setText(getString(R.string.manually));
            buttonRight.setText(getString(R.string.locateMe));
            buttonRight.setVisibility(View.VISIBLE);
        } else {
            // still pages are left
            buttonLeft.setText(getString(R.string.start));
            buttonRight.setVisibility(View.GONE);
        }

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.buttonLeft:
                        // checking for last page
                        // if last page account_menu screen will be launched
                        specifiedPosition = getArguments().getInt(ARG_SECTION_NUMBER) + 1;

                        if (specifiedPosition < layouts.length) {
                            connection.moveScreen(specifiedPosition);
                        } else {
                            connection.launchHomeActivity();
                        }
                        break;

                    case R.id.buttonRight:
                        connection.getLocationData();
                        break;

                    default:
                        break;
                }
            }
        };

        buttonLeft.setOnClickListener(onClickListener);
        buttonRight.setOnClickListener(onClickListener);
    }

}
