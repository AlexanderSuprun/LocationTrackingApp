package com.example.locationtrackingapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.locationtrackingapp.R;
import com.example.locationtrackingapp.databinding.FragmentSettingsBinding;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding mBinding;
    private NavController mNavController;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        ((Toolbar) mBinding.toolbar.getRoot()).setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.settings_list_item, getResources().getStringArray(R.array.settings_entries));
        mBinding.listViewSettings.setAdapter(adapter);
        mBinding.listViewSettings.setOnItemClickListener((parent, view1, position, id) -> {
            if (position == 0) {
                mNavController.navigate(SettingsFragmentDirections.actionSettingsFragment2ToSettingsAccountFragment());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}