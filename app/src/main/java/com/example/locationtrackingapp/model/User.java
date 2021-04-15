package com.example.locationtrackingapp.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(indices = {@Index(value = {"username"}, unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String imageUri;
    private String name;
    private String surname;
    private String username;
    private int passwordHash;

    public User(String imageUri, String name, String surname, String username, int passwordHash) {
        this.imageUri = imageUri;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(int passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (passwordHash != user.passwordHash) return false;
        if (!imageUri.equals(user.imageUri)) return false;
        if (!name.equals(user.name)) return false;
        if (!surname.equals(user.surname)) return false;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + imageUri.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + passwordHash;
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", imageUri='" + imageUri + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", username='" + username + '\'' +
                ", usernameAndPasswordHash=" + passwordHash +
                '}';
    }
}