package com.example.locationtrackingapp.utils;

import android.content.Context;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.location.LocationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtrackingapp.database.AppDatabase;
import com.example.locationtrackingapp.model.LocationPoint;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;

import static com.example.locationtrackingapp.MainViewModel.userIdForWorkManager;


public class LocationWorker extends Worker {

    public static final String WORKER_NAME = "com.example.locationtrackingapp.WORKER_NAME";
    private final Context mContext;
    private final CancellationTokenSource mCancellationToken;

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
                            if (userIdForWorkManager != 0) {
                                AppDatabase.getExecutors().execute(() -> AppDatabase.getInstance().userDao().insertLocation(
                                        new LocationPoint(userIdForWorkManager, location.getLongitude(), location.getLatitude())));
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            return Result.success();
        } else {
            return Result.failure();
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        mCancellationToken.cancel();
    }
}
