package com.example.locationtrackingapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationtrackingapp.R;
import com.example.locationtrackingapp.app.LocationTrackingApp;
import com.example.locationtrackingapp.model.LocationPoint;

import java.util.ArrayList;
import java.util.List;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {

    private final ArrayList<LocationPoint> mLocationPoints;
    private final Context mContext;

    public LocationRecyclerAdapter(List<LocationPoint> points) {
        this.mLocationPoints = (ArrayList<LocationPoint>) points;
        this.mContext = LocationTrackingApp.getAppContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationPoint point = mLocationPoints.get(position);
        holder.textViewLongitude.setText(mContext.getString(R.string.value_longitude, point.getLongitude()));
        holder.textViewLatitude.setText(mContext.getString(R.string.value_latitude, point.getLatitude()));
    }

    @Override
    public int getItemCount() {
        return mLocationPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textViewLatitude;
        AppCompatTextView textViewLongitude;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLatitude = itemView.findViewById(R.id.text_view_rv_item_latitude);
            textViewLongitude = itemView.findViewById(R.id.text_view_rv_item_longitude);
        }
    }

}
