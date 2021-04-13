package com.example.locationtrackingapp;

import androidx.lifecycle.ViewModel;

import com.example.locationtrackingapp.model.User;

import java.util.List;

public class MainViewModel extends ViewModel {

    private final PrefsManager mPrefsManager;
    private User mLoggedInUser;

    public MainViewModel() {
        mPrefsManager = new PrefsManager();
    }

    public boolean validateSignIn(String username, String password) {
        List<User> userList = mPrefsManager.getUsers();
        for (User user : userList) {
            if (user.getUsername().equals(username)
                    && user.getUsernameAndPasswordHash() == (username.hashCode() + password.hashCode())) {
                mLoggedInUser = user;
                return true;
            }
        }
        return false;
    }

    public void saveUser(User user) {
        mPrefsManager.saveUser(user);
    }

    public void updateUser(User user) {
        mLoggedInUser = user;
        mPrefsManager.updateUser(user, mUserIndex);
    }

    public User getLoggedInUser() {
        return mLoggedInUser;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

    }
}