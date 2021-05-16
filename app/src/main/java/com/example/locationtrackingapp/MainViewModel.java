package com.example.locationtrackingapp;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.locationtrackingapp.model.LocationPoint;
import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.utils.LocationWorker;
import com.example.locationtrackingapp.utils.SyncWorker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.locationtrackingapp.utils.LocationWorker.LOCATION_WORKER_NAME;
import static com.example.locationtrackingapp.utils.SyncWorker.SYNC_WORKER_NAME;

public class MainViewModel extends AndroidViewModel {

    public static String userIdForWorkManager;
    private final MainRepository mRepository;
    private final LiveData<User> mUser;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mUser = new MutableLiveData<>();
        mRepository = new MainRepository();
    }

    public void signInWithEmailAndPassword(String email, String password) {
        mRepository.signInWithEmailAndPassword(email, password);
    }

    public void saveUser(User user, String password, Uri imageUrl) {
        mRepository.saveUser(user, password, imageUrl);
    }

    public void updateUser(Uri imageUri, String name, String surname) {
        mRepository.updateUser(imageUri, name, surname);
    }

    public void updateUser(Uri imageUri, String name, String surname, String email,
                           String oldPassword, String newPassword) {
        mRepository.updateUser(imageUri, name, surname);
        mRepository.updateEmailAndPassword(email, oldPassword, newPassword);
    }

    public void updateUser(Uri imageUri, String name, String surname, String email, String oldPassword) {
        mRepository.updateUser(imageUri, name, surname);
        mRepository.updateEmail(email, oldPassword);
    }

    public LiveData<List<LocationPoint>> getSavedLocations() {
        return mRepository.getLocations();
    }

    public void startWorkManager() {
        if (mUser.getValue() != null) {
            userIdForWorkManager = mUser.getValue().getUserId();
            PeriodicWorkRequest locationWorkRequest = new PeriodicWorkRequest.Builder(LocationWorker.class,
                    15, TimeUnit.MINUTES)
                    .build();
            PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(SyncWorker.class,
                    24, TimeUnit.HOURS)
                    .build();
            WorkManager workManager = WorkManager.getInstance(getApplication());
            workManager.enqueueUniquePeriodicWork(LOCATION_WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, locationWorkRequest);
            workManager.enqueueUniquePeriodicWork(SYNC_WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, syncWorkRequest);
        }
    }

//    public void stopWorkManager() {
//        WorkManager.getInstance(getApplication()).cancelUniqueWork(LOCATION_WORKER_NAME);
//        WorkManager.getInstance(getApplication()).cancelUniqueWork(SYNC_WORKER_NAME);
//    }

    public LiveData<User> getLoggedInUser() {
        return mRepository.getUser();
    }

    public void signOutUser() {
        mRepository.signOutUser();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}