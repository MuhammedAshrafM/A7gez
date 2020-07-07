package com.example.muhammed.a7gez;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Mu7ammed_A4raf on 06-Dec-17.
 */

class RecyclerViewPlacesAdapter extends RecyclerView.Adapter<ViewHolderPlaces> {

    private ArrayList<Place> placeArrayList;
    private Context context;
    private StringBuffer cuisines, beverages;
    private Intent intent;
    private String placeId;


    public RecyclerViewPlacesAdapter(Context context, ArrayList<Place> placeArrayList) {
        this.context = context;
        this.placeArrayList = placeArrayList;
        this.cuisines = new StringBuffer();
        this.beverages = new StringBuffer();

        intent = new Intent(context,PlaceActivity.class);
    }

    @Override
    public ViewHolderPlaces onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_places, parent, false);

        ViewHolderPlaces vh = new ViewHolderPlaces(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolderPlaces holder, int position) {

        cuisines = new StringBuffer();
        beverages = new StringBuffer();

        String placeName = placeArrayList.get(position).getName();
        String placeType = placeArrayList.get(position).getType();
        String placeImagePath = placeArrayList.get(position).getImagePath();
//        String areaName = placeArrayList.get(position).getAreaName();
//        String cityName = placeArrayList.get(position).getCityName();
        Double placeRate = placeArrayList.get(position).getRate();
        Long numberUsers = placeArrayList.get(position).getNumberUsers();
        ArrayList<String> cuisines = placeArrayList.get(position).getCuisines();
        ArrayList<String> beverages = placeArrayList.get(position).getBeverages();
        Map<String, Object> opening_hours = placeArrayList.get(position).getOpening_hours();

        holder.ratingBarPlaceRate.setRating(Float.valueOf(String.valueOf(placeRate)));
        holder.textViewNumberUsers.setText("(" + numberUsers + ")");
        holder.textViewPlaceName_type.setText(placeName + " (" + placeType + ")");

        if (placeType.equals("Restaurant")) {
            for (String food : cuisines) {
                this.cuisines.append(food + ", ");
            }
            holder.textViewPlaceCuisines_beverages.setText(this.cuisines.substring(0, this.cuisines.lastIndexOf(", ")));
        } else if (placeType.equals("Coffee")) {
            for (String drink : beverages) {
                this.beverages.append(drink + ", ");
            }
            holder.textViewPlaceCuisines_beverages.setText(this.beverages.substring(0, this.beverages.lastIndexOf(", ")));
        }

        // check on work hours for the place
        if (getCurrentTimestamp(opening_hours)) {
            holder.textViewClosed.setVisibility(View.VISIBLE);
        }

        CustomPicasso.loadImage(context, placeImagePath, holder.imageViewPlaceImage);


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                placeId = placeArrayList.get(position).getId();
                intent.putExtra("placeId",placeId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }


    public void setFilter(List<Place> placeList) {
        placeArrayList = new ArrayList<>();
        placeArrayList.addAll(placeList);
        notifyDataSetChanged();
    }

    private boolean getCurrentTimestamp(Map<String, Object> opening_hours) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        String currentTime = simpleTimeFormat.format(calendar.getTime());

        for (Object day : opening_hours.keySet()) {
            if (day.toString().contains(currentDate)) {
                try {
                    Date dateCurrent = simpleTimeFormat.parse(currentTime);
                    Date dateFrom = simpleTimeFormat.parse(opening_hours.get(day).toString().substring(0, 4));
                    Date dateTo = simpleTimeFormat.parse(opening_hours.get(day).toString().substring(8));

                    if ((dateCurrent.after(dateFrom) || dateCurrent.equals(dateFrom)) && (dateCurrent.before(dateTo) || dateCurrent.equals(dateTo))) {
                        return false;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }


}
