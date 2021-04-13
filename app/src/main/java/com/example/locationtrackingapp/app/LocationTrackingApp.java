package com.example.locationtrackingapp.app;

import android.app.Application;
import android.content.Context;

import com.example.locationtrackingapp.database.AppDatabase;

import java.lang.ref.WeakReference;

public class LocationTrackingApp extends Application {

    private static WeakReference<Context> sContext;

    public static Context getAppContext() {
        return sContext.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = new WeakReference<>(this);
    }
}
