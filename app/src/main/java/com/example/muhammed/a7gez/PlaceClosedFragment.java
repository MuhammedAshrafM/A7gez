package com.example.muhammed.a7gez;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlaceClosedFragment extends Fragment {

    private View view;
    private Toolbar toolbar;
    private String placeName, placeType;
    private TextView textView, textView2;
    private Button buttonContinue;

    private View.OnClickListener onClickListener;
    private FragmentPlaceClosedConnection connection;


    public PlaceClosedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null){
            view = inflater.inflate(R.layout.fragment_place_closed, container, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initiate all views
        initControls();
    }


    private void initControls() {

        try {
            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Info");
        } catch (Exception e) {
            Log.d("toolbar",e.toString());
        }
        textView = (TextView) view.findViewById(R.id.textView);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        buttonContinue = (Button)view.findViewById(R.id.Continue);

        connection = (FragmentPlaceClosedConnection) getActivity();

        textView.setText(placeName + " " + placeType + " is currently closed.");
        textView2.setText("To order in " + placeName + ", have a look at our opening hours in the info section.");

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.Continue:
                        connection.openPlaceInfo();
                        break;

                    default:
                        break;
                }
            }
        };

        buttonContinue.setOnClickListener(onClickListener);
    }


    public void setPlaceData(String placeName, String placeType) {
        this.placeName = placeName;
        this.placeType = placeType;
    }

}
