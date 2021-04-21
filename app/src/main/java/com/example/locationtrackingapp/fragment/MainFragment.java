package com.example.locationtrackingapp.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
import com.example.locationtrackingapp.utils.LocationRecyclerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * MainFragment with DrawerLayout.
 */
public class MainFragment extends Fragment {

    private FragmentMainBinding mBinding;
    private NavController mNavController;
    private MainViewModel mViewModel;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayoutManager layoutManager;
    private LocationRecyclerAdapter adapter;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new LocationRecyclerAdapter(new ArrayList<>());
        mNavController = Navigation.findNavController(view);
        NavHeaderBinding mNavHeaderBinding = NavHeaderBinding.bind(mBinding.navigationView.getHeaderView(0));

        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);

        mViewModel.getSavedLocations().observe(getViewLifecycleOwner(), userWithLocations -> {
            Log.i("TAG_DEBUG", "Saved locations observer");
            adapter.setNewItems(userWithLocations.locations);
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(adapter.getItemCount() - 1);
        });

        mViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                mNavHeaderBinding.setUser(user);
                Glide.with(this)
                        .load(user.getImageUri())
                        .centerCrop()
                        .into(mNavHeaderBinding.imageView);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(requireActivity(), mBinding.drawerLayout,
                (Toolbar) mBinding.toolbar.getRoot(), R.string.open_drawer, R.string.closed_drawer);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mDrawerToggle);
        mBinding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_drawer_menu_settings) {
                item.setChecked(true);
                mBinding.drawerLayout.closeDrawers();
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
    public void onStart() {
        super.onStart();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mViewModel.stopWorkManager();
    }
}