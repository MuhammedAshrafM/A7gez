package com.example.muhammed.a7gez;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mu7ammed_A4raf on 06-Dec-17.
 */

class RecyclerViewTableAdapter extends RecyclerView.Adapter<ViewHolderTable> {

    private ArrayList<Map<String,Object>> tableArrayList;
    private Context context;
    private Intent intent;
    private String tableId;


    public RecyclerViewTableAdapter(Context context, ArrayList<Map<String,Object>> tableArrayList) {
        this.context = context;
        this.tableArrayList = tableArrayList;

//        intent = new Intent(context,PlaceActivity.class);

    }

    @Override
    public ViewHolderTable onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_table, parent, false);

        ViewHolderTable vh = new ViewHolderTable(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolderTable holder, int position) {

        boolean tableAvailable = (boolean) tableArrayList.get(position).get("available");
        long seatsNumber = (long) tableArrayList.get(position).get("seatsNumber");

        holder.textViewSeatsNumber.setText("Seats: "+ seatsNumber);

        if (tableAvailable) {
            holder.textViewTableAvailable.setVisibility(View.VISIBLE);
            holder.textViewTableUnavailable.setVisibility(View.GONE);
        } else {

            holder.textViewTableAvailable.setVisibility(View.GONE);
            holder.textViewTableUnavailable.setVisibility(View.VISIBLE);
        }


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                boolean tableAvailable = (boolean) tableArrayList.get(position).get("available");
                if(tableAvailable) {
                    tableId = (String) tableArrayList.get(position).get("tableID");
//                        intent.putExtra("tableID", tableId);
//                        context.startActivity(intent);
                }else {
                    Toast.makeText(context, "Sorry, this table is reserved choose another table", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableArrayList.size();
    }


    public void setFilter(List<Map<String,Object>> list) {
        tableArrayList = new ArrayList<>();
        tableArrayList.addAll(list);
        notifyDataSetChanged();
    }


}
