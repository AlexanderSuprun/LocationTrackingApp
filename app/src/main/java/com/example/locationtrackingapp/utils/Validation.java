package com.example.locationtrackingapp.utils;

import com.example.locationtrackingapp.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-zA-Z]).{8,20}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public static boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}