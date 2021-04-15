package com.example.locationtrackingapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.locationtrackingapp.model.LocationPoint;
import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.model.UserIdAndUsername;
import com.example.locationtrackingapp.model.UserWithLocations;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE username LIKE :username AND passwordHash LIKE :passwordHash")
    User findUserByUsernameAndPassword(String username, int passwordHash);

    @Transaction
    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<UserWithLocations> getLocationsForUser(int id);

    @Query("SELECT id, username FROM user WHERE username LIKE :username")
    UserIdAndUsername getUserByUsername(String username);

    @Insert
    void insertLocation(LocationPoint location);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
