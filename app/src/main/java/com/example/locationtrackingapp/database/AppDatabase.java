package com.example.locationtrackingapp.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.locationtrackingapp.app.LocationTrackingApp;
import com.example.locationtrackingapp.model.User;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "users_database";
    private static final byte NUMBER_OF_THREADS = 2;
    private static AppDatabase sDatabase = null;
    private static ExecutorService sExecutor = null;

    public static synchronized AppDatabase getInstance() {
        if (sDatabase == null) {
            sDatabase = Room.databaseBuilder(LocationTrackingApp.getAppContext(), AppDatabase.class, DATABASE_NAME)
                    .build();
        }
        return sDatabase;
    }

    private static Executor getExecutors() {
        if (sExecutor == null) {
            sExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        }
        return sExecutor;
    }

    public abstract UserLocationDao userLocationDao();

}
