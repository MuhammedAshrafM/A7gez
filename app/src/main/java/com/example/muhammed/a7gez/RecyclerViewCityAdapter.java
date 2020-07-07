package com.example.muhammed.a7gez;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mu7ammed_A4raf on 06-Dec-17.
 */

class RecyclerViewCityAdapter extends RecyclerView.Adapter<ViewHolderLocation> {

    private ArrayList<City> cityArrayList;

    private FragmentLocationsConnection connection;

    public RecyclerViewCityAdapter(Context context, ArrayList<City> cityArrayList) {
        this.cityArrayList = cityArrayList;

        connection = (FragmentLocationsConnection)context;
    }

    @Override
    public ViewHolderLocation onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_location, parent, false);

        ViewHolderLocation vh = new ViewHolderLocation(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolderLocation holder, int position) {

        String cityName = cityArrayList.get(position).getCityName();
        String cityId = cityArrayList.get(position).getCityId();
        ArrayList<String> areas = cityArrayList.get(position).getAreas();

        holder.textViewLocationAddress.setText(cityName);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                // pass City object as parameter to HomeActivity by using the interface
                connection.replaceFragment(cityArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityArrayList.size();
    }


    public void setFilter(List<City> cityList){
        cityArrayList =new ArrayList<>();
        cityArrayList.addAll(cityList);
        notifyDataSetChanged();
    }

}
