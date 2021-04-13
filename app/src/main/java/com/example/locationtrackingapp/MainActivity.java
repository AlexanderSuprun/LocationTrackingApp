package com.example.locationtrackingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private NavController mNavController;
    private Bundle mSavedNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainViewModel model = new ViewModelProvider(this).get(MainViewModel.class);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mNavController = navHostFragment.getNavController();
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
}