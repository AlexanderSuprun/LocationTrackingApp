package com.example.locationtrackingapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.locationtrackingapp.database.AppDatabase;
import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.model.UserWithLocations;

import java.util.List;

public class MainRepository {

    private final MutableLiveData<User> mUser;
    private final MutableLiveData<List<UserWithLocations>> mUserWithLocations;
    private final AppDatabase mDataBase;

    public MainRepository() {
        mDataBase = AppDatabase.getInstance();
        mUser = new MutableLiveData<>();
        mUserWithLocations = new MutableLiveData<>();
    }

    public void saveUser(User user) {
        AppDatabase.getExecutors().execute(() -> mDataBase.runInTransaction(() -> {
            mDataBase.userDao().insertUser(user);
            user.id = mDataBase.userDao().getUserByUsername(user.getUsername()).id;
            mUser.postValue(user);
        }));
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

    public LiveData<List<UserWithLocations>> getLocationsForUser(int id) {
        AppDatabase.getExecutors().execute(() ->
                mUserWithLocations.postValue(mDataBase.userDao().getLocationsForUser(id)));
        return mUserWithLocations;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }
}
