package com.example.muhammed.a7gez;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

public class ViewHolderPlaceReviews extends RecyclerView.ViewHolder{

    TextView textViewUserName;
    TextView textViewReviewTime;
    TextView textViewReviewText;
    TextView textViewSuggestedReviewText;
    RatingBar ratingBar;


    public ViewHolderPlaceReviews(View itemView) {
        super(itemView);

        textViewUserName = (TextView) itemView.findViewById(R.id.userName);
        textViewReviewTime = (TextView) itemView.findViewById(R.id.reviewTime);
        textViewReviewText = (TextView) itemView.findViewById(R.id.reviewText);
        textViewSuggestedReviewText = (TextView) itemView.findViewById(R.id.suggestedReviewText);
        ratingBar = (RatingBar) itemView.findViewById(R.id.rate);

    }

}
