package com.example.locationtrackingapp.utils;

import com.example.locationtrackingapp.model.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private static final String regex = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,20}$";
    private final PrefsManager mPrefsManager;

    public Validation() {
        this.mPrefsManager = new PrefsManager();
    }

    public static boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isUserNameAvailable(String username) {
        List<User> userList = mPrefsManager.getUsers();
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }
}