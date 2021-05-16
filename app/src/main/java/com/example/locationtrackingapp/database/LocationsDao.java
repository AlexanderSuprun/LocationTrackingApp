package com.example.locationtrackingapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.locationtrackingapp.model.LocationPoint;

import java.util.List;

@Dao
public interface LocationsDao {

    @Query("SELECT * FROM location_points")
    LiveData<List<LocationPoint>> getAllLocations();

    @Query("SELECT * FROM location_points WHERE locationId > :id")
    LiveData<List<LocationPoint>> getLocationsStartFromId(long id);

    @Insert
    void insertLocation(LocationPoint location);

    @Update
    void updateLocations(LocationPoint locationPoint);

    @Delete
    void deleteLocation(LocationPoint locationPoint);
}
