package com.example.locationtrackingapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.locationtrackingapp.MainViewModel;
import com.example.locationtrackingapp.R;
import com.example.locationtrackingapp.databinding.FragmentSettingsAccountBinding;
import com.example.locationtrackingapp.model.User;
import com.example.locationtrackingapp.utils.Validation;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;

public class SettingsAccountFragment extends Fragment {

    private FragmentSettingsAccountBinding mBinding;
    private NavController mNavController;
    private MainViewModel mViewModel;
    private int mLoggedUserPasswordHash;
    private int mLoggedUserId;
    private String mImageUri;
    private Validation mValidation;

    public SettingsAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValidation = new Validation();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentSettingsAccountBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Toolbar) mBinding.toolbar.getRoot()).setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        mNavController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), user -> {
            mLoggedUserId = user.id;
            mLoggedUserPasswordHash = user.getPasswordHash();
            mImageUri = user.getImageUri();
            mBinding.editTextName.setText(user.getName());
            mBinding.editTextSurname.setText(user.getSurname());
            mBinding.editTextUsername.setText(user.getUsername());
            Glide.with(this)
                    .load(mImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(mBinding.imageButtonPickImage);
        });
    }

    public void onImagePickClick() {
        ImagePicker.Companion.with(this)
                .start();
    }

    public void onSaveClick() {
        if (TextUtils.isEmpty(mBinding.editTextName.getText())) {
            clearAllErrors();
            mBinding.editTextNameLayout.setError(getString(R.string.error_fill_in_all_fields));
        } else if (TextUtils.isEmpty(mBinding.editTextSurname.getText())) {
            clearAllErrors();
            mBinding.editTextSurnameLayout.setError(getString(R.string.error_fill_in_all_fields));
        } else if (TextUtils.isEmpty(mBinding.editTextUsername.getText())) {
            clearAllErrors();
            mBinding.editTextUsernameLayout.setError(getString(R.string.error_fill_in_all_fields));
        } else if (!mValidation.isUserNameAvailable(mBinding.editTextUsername.getText().toString(), mLoggedUserId)) {
            clearAllErrors();
            mBinding.editTextUsernameLayout.setError(getString(R.string.error_username_taken));
        } else if ((TextUtils.isEmpty(mBinding.editTextPassword.getText())) &&
                (TextUtils.isEmpty(mBinding.editTextVerifyPassword.getText()))) {
            clearAllErrors();
            mViewModel.updateUser(new User(
                    mImageUri,
                    mBinding.editTextName.getText().toString().trim(),
                    mBinding.editTextSurname.getText().toString().trim(),
                    mBinding.editTextUsername.getText().toString().trim(),
                    mLoggedUserPasswordHash));
            mNavController.navigateUp();
        } else if (!mBinding.editTextPassword.getText().toString().trim()
                .equals(mBinding.editTextVerifyPassword.getText().toString().trim())) {
            clearAllErrors();
            mBinding.editTextPasswordLayout.setError(getString(R.string.error_fields_do_not_match));
            mBinding.editTextVerifyPasswordLayout.setError(getString(R.string.error_fields_do_not_match));
        } else if (!Validation.isPasswordValid(mBinding.editTextPassword.getText().toString().trim())) {
            clearAllErrors();
            mBinding.editTextPasswordLayout.setError(getString(R.string.error_not_valid_password));
            mBinding.editTextVerifyPasswordLayout.setError(getString(R.string.error_not_valid_password));
        } else {
            mViewModel.updateUser(new User(
                    mImageUri,
                    mBinding.editTextName.getText().toString().trim(),
                    mBinding.editTextSurname.getText().toString().trim(),
                    mBinding.editTextUsername.getText().toString().trim(),
                    mBinding.editTextPassword.getText().toString().trim().hashCode()));
            mNavController.navigateUp();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            mImageUri = data.getData().toString();
            Glide.with(this)
                    .load(mImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(mBinding.imageButtonPickImage);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireActivity(), R.string.toast_task_cancelled, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAllErrors() {
        mBinding.editTextNameLayout.setError(null);
        mBinding.editTextSurnameLayout.setError(null);
        mBinding.editTextUsernameLayout.setError(null);
        mBinding.editTextPasswordLayout.setError(null);
        mBinding.editTextVerifyPasswordLayout.setError(null);
    }

    public void clearPasswordError() {
        mBinding.editTextPasswordLayout.setError(null);
        mBinding.editTextVerifyPasswordLayout.setError(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}