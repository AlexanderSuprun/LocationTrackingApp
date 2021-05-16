package com.example.locationtrackingapp.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtrackingapp.database.AppDatabase;
import com.example.locationtrackingapp.model.LocationPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SyncWorker extends Worker {

    public static final String SYNC_WORKER_NAME = "com.example.locationtrackingapp.SYNC_WORKER_NAME";
    private static final String COLLECTION_LOCATIONS = "locations";
    private static final String DOCUMENT_USER_LOCATIONS = "user-locations";
    private static final String FIELD_LOCATION_ID = "locationId";
    public final String TAG = "TAG_DEBUG_" + getClass().getSimpleName();
    private final FirebaseFirestore mFirestore;
    private final FirebaseAuth mAuth;
    private Observer<List<LocationPoint>> mObserver;
    private int mLastLocationId;
    private LocationPoint mLastPoint;
    private LiveData<List<LocationPoint>> mSavedLocations;

    public SyncWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        CollectionReference userCollection = mFirestore.collection(COLLECTION_LOCATIONS)
                .document(DOCUMENT_USER_LOCATIONS)
                .collection(mAuth.getCurrentUser().getUid());
        userCollection.orderBy(FIELD_LOCATION_ID, Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        Log.d(TAG, "Snapshot value: " + value);
                        mLastPoint = value.getDocuments().get(0).toObject(LocationPoint.class);
                        if (mLastPoint != null)
                            mLastLocationId = mLastPoint.locationId;
                        Log.d(TAG, "Last point: " + mLastPoint);
                        AppDatabase.getExecutors().execute(() -> mSavedLocations = AppDatabase.getInstance().locationsDao().getLocationsStartFromId(mLastLocationId));
                    } else {
                        AppDatabase.getExecutors().execute(() -> mSavedLocations = AppDatabase.getInstance().locationsDao().getAllLocations());
                    }
                });

        mSavedLocations.observeForever(mObserver = locationPoints -> {
            if (locationPoints != null) {
                for (LocationPoint point : locationPoints) {
                    userCollection.add(point);
                }
            }
        });

        return Result.success();
    }

    @Override
    public void onStopped() {
        mSavedLocations.removeObserver(mObserver);
        super.onStopped();
    }
}
