package com.example.locationtrackingapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "location_points")
public class LocationPoint {

    @PrimaryKey(autoGenerate = true)
    public int locationId;
    private int userId;
    private double longitude;
    private double latitude;

    public LocationPoint(int userId, double longitude, double latitude) {
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationPoint that = (LocationPoint) o;

        if (locationId != that.locationId) return false;
        if (userId != that.userId) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        return Double.compare(that.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = locationId;
        result = 31 * result + userId;
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "LocationPoint{" +
                "locationId=" + locationId +
                ", userId=" + userId +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
