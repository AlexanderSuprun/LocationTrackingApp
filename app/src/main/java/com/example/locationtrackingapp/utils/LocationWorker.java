package com.example.locationtrackingapp.utils;

import android.content.Context;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;


public class LocationWorker extends Worker {

    public static final String WORKER_TAG = "com.example.locationtrackingapp.WORKER_TAG";
    public static final String WORKER_OUTPUT_LONGITUDE = "com.example.locationtrackingapp.WORKER_OUTPUT_LONGITUDE";
    public static final String WORKER_OUTPUT_LATITUDE = "com.example.locationtrackingapp.WORKER_OUTPUT_LATITUDE";
    private final Context mContext;
    private final CancellationTokenSource mCancellationToken;
    private double mLongitude;
    private double mLatitude;

    public LocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mContext = context;
        mCancellationToken = new CancellationTokenSource();
    }

    @NonNull
    @Override
    public Result doWork() {
        if (LocationManagerCompat.isLocationEnabled((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE))) {
            try {
                LocationServices.getFusedLocationProviderClient(mContext)
                        .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, mCancellationToken.getToken())
                        .addOnSuccessListener(location -> {
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            return Result.success(new Data.Builder()
                    .putDouble(WORKER_OUTPUT_LATITUDE, mLatitude)
                    .putDouble(WORKER_OUTPUT_LONGITUDE, mLongitude)
                    .build());
        } else {
            return Result.retry();
        }
    }
}
