package com.example.locationtrackingapp.utils;

import android.util.Log;

import com.example.locationtrackingapp.database.AppDatabase;
import com.example.locationtrackingapp.model.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private static final String regex = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,20}$";
    private AppDatabase mDatabase;
    private List<User> userList;

    public Validation() {
        mDatabase = AppDatabase.getInstance();
        AppDatabase.getExecutors().execute(() -> userList = mDatabase.userDao().getAllUsers());
    }

    public static boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * Use this method for registration.
     *
     * @param username username to check.
     * @return true if username is available.
     */

    public boolean isUserNameAvailable(String username) {
        Log.i("TAG_VALIDATION", "userList size: " + userList.size());
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Use this method for account settings.
     *
     * @param username username to check.
     * @param userId   user's id.
     * @return true if username is available.
     */
    public boolean isUserNameAvailable(String username, int userId) {
        Log.i("TAG_VALIDATION", "userList size: " + userList.size());
        for (User user : userList) {
            if (user.getUsername().equals(username) && user.id != userId) {
                return false;
            }
        }
        return true;
    }
}