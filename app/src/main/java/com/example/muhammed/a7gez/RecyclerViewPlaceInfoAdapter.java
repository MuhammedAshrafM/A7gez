package com.example.muhammed.a7gez;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Mu7ammed_A4raf on 06-Dec-17.
 */

class RecyclerViewPlaceInfoAdapter extends RecyclerView.Adapter<ViewHolderPlaceInfo> {

    private ArrayList<String> arrayList;
    private ArrayList<StringBuffer> openingHoursArrayList;


    public RecyclerViewPlaceInfoAdapter(ArrayList<String> arrayList, ArrayList<StringBuffer> openingHoursArrayList) {

        this.arrayList = arrayList;
        this.openingHoursArrayList = openingHoursArrayList;

    }

    public RecyclerViewPlaceInfoAdapter(ArrayList<String> arrayList) {

        this.arrayList = arrayList;
        this.openingHoursArrayList = null;

    }

    @Override
    public ViewHolderPlaceInfo onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_place_info, parent, false);

        ViewHolderPlaceInfo vh = new ViewHolderPlaceInfo(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolderPlaceInfo holder, int position) {

        // the first condition set data of work hours for the place
        // the second condition set data of payment methods for the place
        if(openingHoursArrayList !=null) {
            String day = arrayList.get(position);
            String openingHours = openingHoursArrayList.get(position).toString();

            holder.textView.setText(day);
            holder.textView2.setText(openingHours);
        }else {
            String paymentMethod = arrayList.get(position);

            holder.textView.setText(paymentMethod);
            holder.textView2.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
