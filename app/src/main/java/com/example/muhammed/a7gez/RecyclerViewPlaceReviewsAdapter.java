package com.example.muhammed.a7gez;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by Mu7ammed_A4raf on 06-Dec-17.
 */

class RecyclerViewPlaceReviewsAdapter extends RecyclerView.Adapter<ViewHolderPlaceReviews> {

    private ArrayList<Map<String,Object>> reviewArrayList;
    private SimpleDateFormat simpleTimeFormat;


    public RecyclerViewPlaceReviewsAdapter(ArrayList<Map<String,Object>> reviewArrayList) {
        this.reviewArrayList = reviewArrayList;
        simpleTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    }

    @Override
    public ViewHolderPlaceReviews onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_place_reviews, parent, false);

        ViewHolderPlaceReviews vh = new ViewHolderPlaceReviews(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolderPlaceReviews holder, int position) {

//        String userID = reviewArrayList.get(position).get("userID").toString();
        String userName = reviewArrayList.get(position).get("userName").toString();
        String reviewText = reviewArrayList.get(position).get("reviewText").toString();
        String suggestedReviewText = reviewArrayList.get(position).get("suggestedReviewText").toString();
        Double rate = (Double) reviewArrayList.get(position).get("rate");
        Date reviewTime = (Date) reviewArrayList.get(position).get("reviewTime");

        String reviewDateTime = simpleTimeFormat.format(reviewTime);


        holder.textViewUserName.setText(userName);
        holder.textViewReviewTime.setText(reviewDateTime);
        holder.textViewReviewText.setText(reviewText);
        holder.textViewSuggestedReviewText.setText(suggestedReviewText);
        holder.ratingBar.setRating(Float.parseFloat(rate.toString()));

    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }


}
