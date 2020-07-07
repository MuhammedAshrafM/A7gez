package com.example.muhammed.a7gez;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class LocationsFragment extends Fragment {

    private View view;
    private String hint;
    private ImageButton imageButtonBack;
    private EditText editTextLocationAddress;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewLocations;
    private RecyclerView.LayoutManager layoutManagerRecycler;
    private RecyclerViewCityAdapter cityAdapter;
    private RecyclerViewAreaAdapter areaAdapter;
    private View.OnClickListener onClickListener;
    private FragmentLocationsConnection connection;
    private ArrayList<City> cityArrayList = null;
    private ArrayList<String> areas = null;

    public LocationsFragment() {
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
            view = inflater.inflate(R.layout.fragment_locations, container, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initControls();
    }


    private void initControls() {

        imageButtonBack = (ImageButton)view.findViewById(R.id.back);
        editTextLocationAddress = (EditText)view.findViewById(R.id.locationAddress);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        recyclerViewLocations = (RecyclerView)view.findViewById(R.id.locations);
        layoutManagerRecycler =new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerViewLocations.setLayoutManager(layoutManagerRecycler);

        connection = (FragmentLocationsConnection)getActivity();
        editTextLocationAddress.setHint(this.hint);

        if(areas != null){
            areaAdapter=new RecyclerViewAreaAdapter(getActivity(), areas);
            recyclerViewLocations.setAdapter(areaAdapter);
            progressBar.setVisibility(View.GONE);
        }



        editTextLocationAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(hint.equals("City")){
                    if(cityArrayList != null) {

                        final List<City> filterModeList = filterCity(cityArrayList, charSequence.toString());
                        cityAdapter.setFilter(filterModeList);
                    }
                }else if (hint.equals("Area")){
                    if(areas != null) {

                        final List<String> filterModeList = filterArea(areas, charSequence.toString());
                        areaAdapter.setFilter(filterModeList);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.back:
                        connection.closeFragment();
                        break;

                    default:
                        break;
                }
            }
        };

        imageButtonBack.setOnClickListener(onClickListener);
    }
    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setAvailableCities(ArrayList<City> cityArrayList){
        this.cityArrayList = cityArrayList;

        cityAdapter=new RecyclerViewCityAdapter(getActivity(), cityArrayList);
        recyclerViewLocations.setAdapter(cityAdapter);

        progressBar.setVisibility(View.GONE);
    }

    public void setAvailableAreas(ArrayList<String> areas){
        this.areas = areas;

        if(view != null) {
            areaAdapter = new RecyclerViewAreaAdapter(getActivity(), areas);
            recyclerViewLocations.setAdapter(areaAdapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    private List<City> filterCity (List<City> pl, String query){

        while (query.startsWith(" ")){
            cityAdapter.setFilter(cityArrayList);
            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return cityArrayList;
            }
        }

        query = query.toLowerCase();
        final List<City> filterModeList = new ArrayList<>();
        for(City city : pl){
            final String cityName = city.getCityName().toLowerCase();
            if(cityName.startsWith(query)){
                filterModeList.add(city);
            }
        }
        return filterModeList;
    }

    private List<String> filterArea (List<String> pl, String query){

        while (query.startsWith(" ")){
            areaAdapter.setFilter(areas);
            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return areas;
            }
        }

        query = query.toLowerCase();
        final List<String> filterModeList = new ArrayList<>();
        for(String string: pl){
            final String areaName = string.toLowerCase();
            if(areaName.startsWith(query)){
                filterModeList.add(string);
            }
        }
        return filterModeList;
    }
}
