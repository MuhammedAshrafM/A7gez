package com.example.muhammed.a7gez;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

public class ViewHolderPlaceInfo extends RecyclerView.ViewHolder{

    TextView textView;
    TextView textView2;


    public ViewHolderPlaceInfo(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.text);
        textView2 = (TextView) itemView.findViewById(R.id.text2);

    }

}
