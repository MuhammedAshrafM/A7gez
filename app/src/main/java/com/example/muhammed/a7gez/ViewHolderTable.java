package com.example.muhammed.a7gez;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

public class ViewHolderTable extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView textViewTableAvailable;
    TextView textViewTableUnavailable;
    TextView textViewSeatsNumber;

    ItemClickListener itemClickListener;

    public ViewHolderTable(View itemView) {
        super(itemView);

        textViewTableAvailable = (TextView) itemView.findViewById(R.id.tableAvailable);
        textViewTableUnavailable = (TextView) itemView.findViewById(R.id.tableUnavailable);
        textViewSeatsNumber = (TextView) itemView.findViewById(R.id.seatsNumber);

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
