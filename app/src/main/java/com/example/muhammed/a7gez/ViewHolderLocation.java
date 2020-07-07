package com.example.muhammed.a7gez;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

    public class ViewHolderLocation extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewLocationAddress;

        ItemClickListener itemClickListener;

        public ViewHolderLocation(View itemView) {
            super(itemView);

            textViewLocationAddress = (TextView) itemView.findViewById(R.id.locationAddress);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }
    }
