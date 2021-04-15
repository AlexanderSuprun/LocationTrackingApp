package com.example.locationtrackingapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.model.UserWithLocations;
import com.example.locationtrackingapp.utils.LocationWorker;

import java.util.concurrent.TimeUnit;

import static com.example.locationtrackingapp.utils.LocationWorker.WORKER_NAME;

public class MainViewModel extends AndroidViewModel {

    public static int userIdForWorkManager;
    private final MainRepository mRepository;
    private MutableLiveData<User> mUser;
    private PeriodicWorkRequest mWorkRequest;
    private LiveData<UserWithLocations> mSavedLocations;
    private Observer<User> mSavedLocationsObserver;
    private Observer<User> mManagerObserver;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MainRepository();
        mUser = new MutableLiveData<>();
    }

    public LiveData<User> findUser(String username, String password) {
        mUser = mRepository.getUserByUsernameAndPassword(username, password);
        return mUser;
    }

    public void saveUser(User user) {
        mUser = mRepository.saveUser(user);
    }

    public void updateUser(User user) {
        mUser.getValue().setImageUri(user.getImageUri());
        mUser.getValue().setName(user.getName());
        mUser.getValue().setSurname(user.getSurname());
        mUser.getValue().setUsername(user.getUsername());
        mUser.getValue().setPasswordHash(user.getPasswordHash());
        mRepository.updateUser(mUser.getValue());
    }

    public LiveData<UserWithLocations> getSavedLocations() {
        mUser.observeForever(mSavedLocationsObserver = user -> {
            if (user != null) {
                mSavedLocations = mRepository.getLocationsForUser(user.id);
            }
        });
        return mSavedLocations;
    }

    public void startWorkManager() {
        mUser.observeForever(mManagerObserver = user -> {
            userIdForWorkManager = user.id;
            mWorkRequest = new PeriodicWorkRequest.Builder(LocationWorker.class,
                    15, TimeUnit.MINUTES)
                    .build();
            WorkManager.getInstance(getApplication())
                    .enqueueUniquePeriodicWork(WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, mWorkRequest);
        });
    }

    public void stopWorkManager() {
        WorkManager.getInstance(getApplication()).cancelUniqueWork(WORKER_NAME);
    }

    public LiveData<User> getLoggedInUser() {
        return mUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mUser.removeObserver(mManagerObserver);
        mUser.removeObserver(mSavedLocationsObserver);
    }
}