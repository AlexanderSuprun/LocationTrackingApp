package com.example.locationtrackingapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.locationtrackingapp.model.User;

import java.util.List;

@Dao
public interface UserLocationDao {

    @Query("Select * from user")
    List<User> getAllUsers();

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
