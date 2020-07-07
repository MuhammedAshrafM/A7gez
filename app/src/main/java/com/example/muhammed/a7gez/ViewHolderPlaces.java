package com.example.muhammed.a7gez;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

public class ViewHolderPlaces extends RecyclerView.ViewHolder implements View.OnClickListener {

    RatingBar ratingBarPlaceRate;
    TextView textViewPlaceName_type;
    TextView textViewPlaceCuisines_beverages;
    TextView textViewNumberUsers;
    TextView textViewClosed;
    ImageView imageViewPlaceImage;

    ItemClickListener itemClickListener;

    public ViewHolderPlaces(View itemView) {
        super(itemView);

        ratingBarPlaceRate = (RatingBar) itemView.findViewById(R.id.placeRate);
        textViewPlaceName_type = (TextView) itemView.findViewById(R.id.placeName_type);
        textViewPlaceCuisines_beverages = (TextView) itemView.findViewById(R.id.placeCuisines_beverages);
        textViewNumberUsers = (TextView) itemView.findViewById(R.id.numberUsers);
        textViewClosed = (TextView) itemView.findViewById(R.id.closed);
        imageViewPlaceImage = (ImageView) itemView.findViewById(R.id.placeImage);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }
}
