package com.example.locationtrackingapp.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imageUri;
    private String name;
    private String surname;
    private String username;
    private int usernameAndPasswordHash;

    public User(int id, String imageUri, String name, String surname, String username, int usernameAndPasswordHash) {
        this.id = id;
        this.imageUri = imageUri;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.usernameAndPasswordHash = usernameAndPasswordHash;
    }

    /**
     * @param imageUri                Uri to profile image.
     * @param name                    person's name.
     * @param surname                 person's surname.
     * @param username                profile username.
     * @param usernameAndPasswordHash sum of hash codes of username and password.
     */
    @Ignore
    public User(String imageUri, String name, String surname, String username, int usernameAndPasswordHash) {
        this.imageUri = imageUri;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.usernameAndPasswordHash = usernameAndPasswordHash;
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

    public int getUsernameAndPasswordHash() {
        return usernameAndPasswordHash;
    }

    public void setUsernameAndPasswordHash(int usernameAndPasswordHash) {
        this.usernameAndPasswordHash = usernameAndPasswordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (usernameAndPasswordHash != user.usernameAndPasswordHash) return false;
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
        result = 31 * result + usernameAndPasswordHash;
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
                ", usernameAndPasswordHash=" + usernameAndPasswordHash +
                '}';
    }
}