package com.example.pmu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pmu.R;
import com.example.pmu.models.LocationModel;
import com.example.pmu.utils.TDate;

import java.util.ArrayList;

public class ReservationAdapter extends ArrayAdapter<LocationModel> {
    private ArrayList<LocationModel> items;

    public ReservationAdapter(Context context, ArrayList<LocationModel> items) {
        super(context, R.layout.row_reservation);
        this.items = items;
    }

    @Override
    public int getCount() {
        if (items.size() > 0) {
            return items.size();
        } else {
            return 1;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ItemViewHolder holder;
        if (row == null || !(row.getTag() instanceof ItemViewHolder)) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.row_reservation, parent, false);

            holder = new ItemViewHolder();
            holder.placeNameTextView = row.findViewById(R.id.placeNameTextView);
            holder.categoryNameTextView = row.findViewById(R.id.categoryNameTextView);
            holder.fromTimeTextView = row.findViewById(R.id.fromTimeTextView);


            row.setTag(holder);
        }
        holder = (ItemViewHolder) row.getTag();
        LocationModel locationModel = items.get(position);


        holder.placeNameTextView.setText(locationModel.getPlace());
        holder.categoryNameTextView.setText(locationModel.getCategory());
        holder.fromTimeTextView.setText(String.format("%s - %s", locationModel.getFromDate(), locationModel.getToDate()));

        return row;
    }


    public void updateItems(ArrayList<LocationModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    private static class ItemViewHolder {
        TextView placeNameTextView;
        TextView categoryNameTextView;
        TextView fromTimeTextView;
    }

}