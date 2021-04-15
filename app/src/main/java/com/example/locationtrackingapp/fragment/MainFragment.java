package com.example.locationtrackingapp.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.locationtrackingapp.MainActivity;
import com.example.locationtrackingapp.MainViewModel;
import com.example.locationtrackingapp.R;
import com.example.locationtrackingapp.databinding.FragmentMainBinding;
import com.example.locationtrackingapp.databinding.NavHeaderBinding;
import com.example.locationtrackingapp.model.LocationPoint;
import com.example.locationtrackingapp.utils.LocationRecyclerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * MainFragment with DrawerLayout.
 */
public class MainFragment extends Fragment {

    private FragmentMainBinding mBinding;
    private NavController mNavController;
    private MainViewModel mViewModel;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<LocationPoint> mSavedLocations;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedLocations = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        LocationRecyclerAdapter adapter = new LocationRecyclerAdapter(mSavedLocations, getContext());
        mNavController = Navigation.findNavController(view);
        NavHeaderBinding mNavHeaderBinding = NavHeaderBinding.bind(mBinding.navigationView.getHeaderView(0));
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);

        mViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), user -> {
            mNavHeaderBinding.setUser(user);
            Glide.with(this)
                    .load(user.getImageUri())
                    .centerCrop()
                    .into(mNavHeaderBinding.imageView);
        });

        mViewModel.getSavedLocations().observe(getViewLifecycleOwner(), userWithLocations ->
                mSavedLocations.addAll(userWithLocations.get(0).locations));

        mViewModel.getLocationPoint().observe(getViewLifecycleOwner(), locationPoint -> {
            mSavedLocations.add(locationPoint);
            adapter.notifyItemInserted(adapter.getItemCount());
            layoutManager.scrollToPosition(adapter.getItemCount());
        });

        mDrawerToggle = new ActionBarDrawerToggle(requireActivity(), mBinding.drawerLayout,
                (Toolbar) mBinding.toolbar.getRoot(), R.string.open_drawer, R.string.closed_drawer);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mDrawerToggle);
        mBinding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_drawer_menu_settings) {
                mNavController.navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment());
            } else if (item.getItemId() == R.id.item_drawer_menu_exit) {
                mViewModel.stopWorkManager();
                mNavController.navigate(MainFragmentDirections.actionMainFragmentToLoginFragment());
            }
            item.setChecked(true);
            mBinding.drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) requireActivity()).saveNavigation(mNavController.saveState());
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.navigationView.setCheckedItem(R.id.item_drawer_menu_main);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}