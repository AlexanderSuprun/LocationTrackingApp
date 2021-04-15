package com.example.locationtrackingapp.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UserWithLocations {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "id",
            entityColumn = "userId"
    )
    public List<LocationPoint> locations;
}
