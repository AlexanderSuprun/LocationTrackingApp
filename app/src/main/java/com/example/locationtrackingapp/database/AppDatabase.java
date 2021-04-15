package com.example.locationtrackingapp.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.locationtrackingapp.app.LocationTrackingApp;
import com.example.locationtrackingapp.model.LocationPoint;
import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.model.UserIdAndUsername;
import com.example.locationtrackingapp.model.UserWithLocations;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, LocationPoint.class},
        version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "users_database";
    private static final byte NUMBER_OF_THREADS = 2;
    private static AppDatabase sDatabase = null;
    private static ExecutorService sExecutor = null;

    public static synchronized AppDatabase getInstance() {
        if (sDatabase == null) {
            Log.i("TAG_DB", "Database created");
            sDatabase = Room.databaseBuilder(LocationTrackingApp.getAppContext(), AppDatabase.class, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return sDatabase;
    }

    public static Executor getExecutors() {
        if (sExecutor == null) {
            Log.i("TAG_DB", "Executors created");
            sExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        }
        return sExecutor;
    }

    public abstract UserDao userDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE location_points ADD COLUMN userId INTEGER NOT NULL DEFAULT 0");
        }
    };
}
