package com.example.locationtrackingapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
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
import com.example.locationtrackingapp.utils.Validation;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;

public class SettingsAccountFragment extends Fragment {

    private FragmentSettingsAccountBinding mBinding;
    private NavController mNavController;
    private MainViewModel mViewModel;
    private Uri mImageUri;

    public SettingsAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            mImageUri = Uri.parse(user.getImageUri());
            mBinding.editTextName.setText(user.getName());
            mBinding.editTextSurname.setText(user.getSurname());
            mBinding.editTextEmail.setText(user.getEmail());
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
        } else if (TextUtils.isEmpty(mBinding.editTextEmail.getText())) {
            clearAllErrors();
            mBinding.editTextEmailLayout.setError(getString(R.string.error_fill_in_all_fields));
        } else if (!Validation.isEmailValid(mBinding.editTextEmail.getText().toString())
                || TextUtils.isEmpty(mBinding.editTextEmail.getText())) {
            clearAllErrors();
            mBinding.editTextEmailLayout.setError(getString(R.string.error_invalid_email));
        } else if (mBinding.editTextEmail.getText().toString().equals(mViewModel.getLoggedInUser().getValue().getEmail())
                && TextUtils.isEmpty(mBinding.editTextPassword.getText())
                && TextUtils.isEmpty(mBinding.editTextVerifyPassword.getText())) {
            clearAllErrors();
            // Update without email and password
            mViewModel.updateUser(mImageUri,
                    mBinding.editTextName.getText().toString().trim(),
                    mBinding.editTextSurname.getText().toString().trim());
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
            mViewModel.updateUser(mImageUri,
                    mBinding.editTextName.getText().toString().trim(),
                    mBinding.editTextSurname.getText().toString().trim(),
                    mBinding.editTextEmail.getText().toString().trim(),
                    verifyPasswordDialog(),
                    mBinding.editTextPassword.getText().toString().trim());
            mNavController.navigateUp();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            mImageUri = data.getData();
            Glide.with(this)
                    .load(mImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(mBinding.imageButtonPickImage);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        }
    }

    public String verifyPasswordDialog() {
        final AppCompatEditText editText = new AppCompatEditText(requireContext());
        editText.setHint(getString(R.string.hint_password));
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.verify_password))
                .setMessage(getString(R.string.enter_old_password))
                .setView(editText)
                .setPositiveButton(getString(R.string.btn_text_ok), (dialog, which) -> {
                    if (editText.getText() == null || editText.getText().toString().isEmpty()) {
                        Toast.makeText(requireContext(), getString(R.string.empty_password_error), Toast.LENGTH_SHORT).show();
                    } else if (!Validation.isPasswordValid(editText.getText().toString())) {
                        Toast.makeText(requireContext(), R.string.invalid_password_error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.btn_text_cancel), (dialog, which) -> dialog.dismiss())
                .show();
        return editText.getText().toString();
    }

    private void clearAllErrors() {
        mBinding.editTextNameLayout.setError(null);
        mBinding.editTextSurnameLayout.setError(null);
        mBinding.editTextEmailLayout.setError(null);
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