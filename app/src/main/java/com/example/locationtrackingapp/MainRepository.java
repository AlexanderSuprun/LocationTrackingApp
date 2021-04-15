package com.example.locationtrackingapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.locationtrackingapp.database.AppDatabase;
import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.model.UserWithLocations;

public class MainRepository {

    private final MutableLiveData<User> mUser;
    private final AppDatabase mDataBase;
    private LiveData<UserWithLocations> mUserWithLocations;

    public MainRepository() {
        mDataBase = AppDatabase.getInstance();
        mUser = new MutableLiveData<>();
        mUserWithLocations = new MutableLiveData<>();
    }

    public MutableLiveData<User> saveUser(User user) {
        AppDatabase.getExecutors().execute(() -> mDataBase.runInTransaction(() -> {
            mDataBase.userDao().insertUser(user);
            user.id = mDataBase.userDao().getUserByUsername(user.getUsername()).id;
            mUser.postValue(user);
        }));
        return mUser;
    }

    public MutableLiveData<User> getUserByUsernameAndPassword(String username, String password) {
        AppDatabase.getExecutors().execute(() ->
                mUser.postValue(mDataBase.userDao().findUserByUsernameAndPassword(username, password.hashCode())));
        return mUser;
    }

    public void updateUser(User user) {
        AppDatabase.getExecutors().execute(() ->
                mDataBase.userDao().updateUser(user));
    }

    public LiveData<UserWithLocations> getLocationsForUser(int id) {
        AppDatabase.getExecutors().execute(() ->
                mUserWithLocations = mDataBase.userDao().getLocationsForUser(id));
        return mUserWithLocations;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }
}
