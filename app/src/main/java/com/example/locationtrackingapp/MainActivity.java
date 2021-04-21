package com.example.locationtrackingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MainActivity extends AppCompatActivity {

    public static final int LOCATION_REQUEST_CODE = 100;
    private boolean mIsPermissionGranted = false;
    private NavController mNavController;
    private Bundle mSavedNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainViewModel model = new ViewModelProvider(this).get(MainViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mNavController = navHostFragment.getNavController();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                mIsPermissionGranted = true;
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageRationale();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSavedNavigation != null) {
            mNavController.restoreState(mSavedNavigation);
        }
    }

    public void saveNavigation(Bundle bundle) {
        this.mSavedNavigation = bundle;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSavedNavigation = null;
    }

    public boolean isPermissionGranted() {
        return mIsPermissionGranted;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                mIsPermissionGranted = true;
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageRationale();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showMessageRationale() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.rationale_message)
                .setPositiveButton(getString(R.string.button_title_allow), (dialog, which) -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE))
                .setNegativeButton(getString(R.string.button_title_cancel), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}