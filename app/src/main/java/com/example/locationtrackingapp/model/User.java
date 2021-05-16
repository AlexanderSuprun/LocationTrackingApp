package com.example.locationtrackingapp.model;

import androidx.annotation.NonNull;

public class User {

    private String userId;
    private String imageUri;
    private String name;
    private String surname;
    private String email;

    public User() {
    }

    public User(String imageUri, String name, String surname, String email) {
        this.imageUri = imageUri;
        this.name = name;
        this.surname = surname;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!userId.equals(user.userId)) return false;
        if (!imageUri.equals(user.imageUri)) return false;
        if (!name.equals(user.name)) return false;
        if (!surname.equals(user.surname)) return false;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + imageUri.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }


    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", username='" + email + '\'' +
                '}';
    }
}