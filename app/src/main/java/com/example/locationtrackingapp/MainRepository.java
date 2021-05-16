package com.example.locationtrackingapp;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.locationtrackingapp.database.AppDatabase;
import com.example.locationtrackingapp.model.LocationPoint;
import com.example.locationtrackingapp.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRepository {

    public static final String COLLECTION_USERS = "users";
    private static final String USER_DIRECTORY = "users";
    private static final String IMAGES_DIRECTORY = "images";
    private final String TAG = "TAG_DEBUG_" + getClass().getSimpleName();
    private final AppDatabase mDataBase;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mFirestore;
    private final StorageReference mUsersDirRef;
    private final MutableLiveData<User> mUser;
    private LiveData<List<LocationPoint>> mLocations;

    public MainRepository() {
        mDataBase = AppDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersDirRef = FirebaseStorage.getInstance().getReference().child(USER_DIRECTORY);
        mLocations = new MutableLiveData<>();
        mUser = new MutableLiveData<>();
        if (mAuth.getCurrentUser() != null) {
            loadUserFromFirebase(mAuth.getCurrentUser().getUid());
        }
    }

    public void saveUser(@NotNull User user, String password, Uri imageUri) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(authResultTask -> {
                    if (authResultTask.isSuccessful()) {
                        Log.d(TAG, "New user created successfully");
                        user.setUserId(mAuth.getCurrentUser().getUid());

                        // Uploading profile picture to FirebaseStorage
                        StorageReference profilePicRef = mUsersDirRef.child(mAuth.getCurrentUser().getUid())
                                .child(IMAGES_DIRECTORY)
                                .child(imageUri.getLastPathSegment());
                        profilePicRef.putFile(imageUri).continueWithTask(task1 -> {
                            if (!task1.isSuccessful() && task1.getException() != null) {
                                throw task1.getException();
                            }
                            // Continue with the task to get the download URL
                            return profilePicRef.getDownloadUrl();

                        }).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                user.setImageUri(task1.getResult().toString());

                                // Creating new user in Firestore
                                mFirestore.collection(COLLECTION_USERS)
                                        .document(mAuth.getCurrentUser().getUid())
                                        .set(user).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        mUser.setValue(user);
                                    }
                                });
                                return;
                            }
                            mUser.setValue(null);
                        });
                        return;
                    }
                    mUser.setValue(null);
                    Log.w(TAG, "Registration failure: " + authResultTask.getException());
                });
    }

    public void updateUser(Uri imageUri, String name, String surname) {
        if (mUser.getValue() != null && !imageUri.toString().equals(mUser.getValue().getImageUri())) {
            StorageReference profilePicRef = mUsersDirRef
                    .child(mAuth.getCurrentUser().getUid())
                    .child(IMAGES_DIRECTORY)
                    .child(imageUri.getLastPathSegment());
            UploadTask uploadTask = profilePicRef.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful() && task.getException() != null) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return profilePicRef.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("surname", surname);
                    userMap.put("imageUri", task.getResult().toString());
                    mFirestore.collection(COLLECTION_USERS)
                            .document(mAuth.getCurrentUser().getUid())
                            .update(userMap)
                            .addOnSuccessListener(unused -> {
                                User user = mUser.getValue();
                                user.setName(name);
                                user.setSurname(surname);
                                user.setImageUri(task.getResult().toString());
                                mUser.setValue(user);
                            });
                }
            });
        } else {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("surname", surname);
            mFirestore.collection(COLLECTION_USERS)
                    .document(mAuth.getCurrentUser().getUid())
                    .update(userMap)
                    .addOnSuccessListener(unused -> {
                        User user = mUser.getValue();
                        user.setName(name);
                        user.setSurname(surname);
                        mUser.setValue(user);
                    });
        }
    }

    public void signInWithEmailAndPassword(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadUserFromFirebase(task.getResult().getUser().getUid());
                    } else {
                        mUser.setValue(null);
                    }
                });
    }

    public void loadUserFromFirebase(String userId) {
        mFirestore.collection(USER_DIRECTORY)
                .document(userId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mUser.setValue(task.getResult().toObject(User.class));
            }
        });
    }

    public void signOutUser() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
    }

    public void updateEmailAndPassword(String email, String oldPassword, String newPassword) {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            mFirestore.collection(COLLECTION_USERS)
                                    .document(mAuth.getCurrentUser().getUid())
                                    .update(userMap)
                                    .addOnSuccessListener(unused -> {
                                        user.updateEmail(email);
                                        user.updatePassword(newPassword);
                                        mUser.getValue().setEmail(email);
                                    });
                        }
                    });
        }
    }

    public void updateEmail(String email, String password) {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updateEmail(email);
                        }
                    });
        }
    }

    public LiveData<List<LocationPoint>> getLocations() {
        AppDatabase.getExecutors().execute(() ->
                mLocations = mDataBase.locationsDao().getAllLocations());
        return mLocations;
    }

    public LiveData<User> getUser() {
        return mUser;
    }
}
