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

class RecyclerViewAreaAdapter extends RecyclerView.Adapter<ViewHolderLocation> {

    private ArrayList<String> areas;

    private FragmentLocationsConnection connection;

    public RecyclerViewAreaAdapter(Context context, ArrayList<String> areas) {
        this.areas = areas;

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
        String areaName = areas.get(position);

        holder.textViewLocationAddress.setText(areaName);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                // pass areaName as parameter to HomeActivity by using the interface
                connection.selectArea(areas.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return areas.size();
    }


    public void setFilter(List<String> areasList){
        areas =new ArrayList<>();
        areas.addAll(areasList);
        notifyDataSetChanged();
    }

}
