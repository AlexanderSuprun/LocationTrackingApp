package com.example.locationtrackingapp;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.BackoffPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.locationtrackingapp.database.AppDatabase;
import com.example.locationtrackingapp.model.LocationPoint;
import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.model.UserWithLocations;
import com.example.locationtrackingapp.utils.LocationWorker;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.example.locationtrackingapp.utils.LocationWorker.WORKER_OUTPUT_LATITUDE;
import static com.example.locationtrackingapp.utils.LocationWorker.WORKER_OUTPUT_LONGITUDE;
import static com.example.locationtrackingapp.utils.LocationWorker.WORKER_TAG;

public class MainViewModel extends AndroidViewModel {

    private final MainRepository mRepository;
    private final WorkManager mWorkManager;
    private final MutableLiveData<LocationPoint> mLocationPoint;
    private MutableLiveData<User> mUser;
    private Observer<List<WorkInfo>> mObserver;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MainRepository();
        mUser = new MutableLiveData<>();
        mLocationPoint = new MutableLiveData<>();
        mWorkManager = WorkManager.getInstance(getApplication());
    }

    public LiveData<User> findUser(String username, String password) {
        mUser = mRepository.getUserByUsernameAndPassword(username, password);
        return mUser;
    }

    public void saveUser(User user) {
        mRepository.saveUser(user);
        mUser = mRepository.getUser();
    }

    public void updateUser(User user) {
        mUser.getValue().setImageUri(user.getImageUri());
        mUser.getValue().setName(user.getName());
        mUser.getValue().setSurname(user.getSurname());
        mUser.getValue().setUsername(user.getUsername());
        mUser.getValue().setPasswordHash(user.getPasswordHash());
        mRepository.updateUser(mUser.getValue());
    }

    public LiveData<List<UserWithLocations>> getSavedLocations() {
        return mRepository.getLocationsForUser(Objects.requireNonNull(mUser.getValue()).id);
    }

    public void startWorkManager() {
        int period = 15;
        PeriodicWorkRequest workRequest;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            workRequest = new PeriodicWorkRequest.Builder(LocationWorker.class, period, TimeUnit.MINUTES)
                    .addTag(WORKER_TAG)
                    .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                            TimeUnit.MILLISECONDS
                    )
                    .build();
        } else {
            workRequest = new PeriodicWorkRequest.Builder(LocationWorker.class, period, TimeUnit.MINUTES)
                    .addTag(WORKER_TAG)
                    .build();
        }
        mWorkManager.enqueue(workRequest);
        mWorkManager.getWorkInfosByTagLiveData(WORKER_TAG)
                .observeForever(mObserver = workInfos -> {
                    Log.i("TAG_VM_WORK", "WorkInfos size:" + workInfos.size());
                    if (workInfos.get(0) != null && workInfos.get(0).getState() == WorkInfo.State.SUCCEEDED) {
                        mLocationPoint.setValue(new LocationPoint(
                                Objects.requireNonNull(mUser.getValue()).id,
                                workInfos.get(0).getOutputData().getDouble(WORKER_OUTPUT_LONGITUDE, 0),
                                workInfos.get(0).getOutputData().getDouble(WORKER_OUTPUT_LATITUDE, 0)));
                    }
                });
    }

    public void stopWorkManager() {
        mWorkManager.getWorkInfosByTagLiveData(WORKER_TAG).removeObserver(mObserver);
        mWorkManager.cancelAllWorkByTag(WORKER_TAG);
    }

    public LiveData<LocationPoint> getLocationPoint() {
        return mLocationPoint;
    }

    public LiveData<User> getLoggedInUser() {
        return mUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}