package com.example.locationtrackingapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Entity;

import com.example.locationtrackingapp.MainActivity;
import com.example.locationtrackingapp.MainViewModel;
import com.example.locationtrackingapp.R;
import com.example.locationtrackingapp.databinding.FragmentLoginBinding;
import com.example.locationtrackingapp.utils.KeyboardUtils;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

@Entity
public class LoginFragment extends Fragment {

    private FragmentLoginBinding mBinding;
    private NavController mNavController;
    private MainViewModel mViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentLoginBinding.inflate(inflater, container, false);
        mBinding.setFragment(this);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onSignInClick() {
        if (TextUtils.isEmpty(mBinding.editTextUsername.getText())) {
            mBinding.editTextPasswordLayout.setError(null);
            mBinding.editTextUsernameLayout.setError(getString(R.string.error_fill_in_all_fields));
        } else if (TextUtils.isEmpty(mBinding.editTextPassword.getText())) {
            mBinding.editTextUsernameLayout.setError(null);
            mBinding.editTextPasswordLayout.setError(getString(R.string.error_fill_in_all_fields));
        } else {
            mViewModel.findUser(mBinding.editTextUsername.getText().toString().trim(), mBinding.editTextPassword.getText().toString().trim())
                    .observe(getViewLifecycleOwner(), user -> {
                        if (user != null) {
                            if (((MainActivity) requireActivity()).isPermissionGranted()) {
                                mViewModel.startWorkManager();
                            }
                            KeyboardUtils.hide(requireView());
                            mNavController.navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment());
                        } else {
                            mBinding.editTextUsernameLayout.setError(getString(R.string.error_invalid_username_or_password));
                            mBinding.editTextPasswordLayout.setError(getString(R.string.error_invalid_username_or_password));
                        }
                    });
        }
    }

    public void onSignUpClick() {
        mNavController.navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment());
    }

    public void clearPasswordError() {
        mBinding.editTextPasswordLayout.setError(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}