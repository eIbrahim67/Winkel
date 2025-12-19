package com.eibrahim.winkel.auth.signup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivitySignupBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SignupFragment extends Fragment {

    private ActivitySignupBinding binding;
    private SignupViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivitySignupBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SignupViewModel.class);

        setupUI();
        observeViewModel();

        return binding.getRoot();
    }

    private void setupUI() {
        binding.btnSignup.setOnClickListener(v -> validateAndSignup());
        binding.btnSignin2.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void validateAndSignup() {
        binding.btnSignup.setEnabled(false);
        binding.btnSignup.setText(R.string.sign_up);
        binding.progressBar.setVisibility(View.VISIBLE);

        String username = binding.nameSignup.getText().toString().trim();
        String email = binding.emailSignup.getText().toString().trim();
        String password = Objects.requireNonNull(binding.passSignup.getText()).toString();
        String rePassword = Objects.requireNonNull(binding.repassSignup.getText()).toString();
        String pin = binding.pinSignup.getText().toString();
        String repin = binding.rePinSignup.getText().toString();
        String phone = binding.phoneSignup.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword) ||
                TextUtils.isEmpty(pin) || TextUtils.isEmpty(repin) ||
                phone.length() != 11 ||
                !password.equals(rePassword) ||
                !pin.equals(repin) ||
                !binding.checkSignup.isChecked()) {

            Snackbar.make(requireView(), getText(R.string.please_fill_valid_details), Snackbar.LENGTH_SHORT).show();
            binding.btnSignup.setEnabled(true);
            binding.btnSignup.setText(R.string.sign_up);
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        viewModel.createAccount(email, password, username, phone, pin);
    }

    private void observeViewModel() {
        viewModel.getSignupSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Snackbar.make(requireView(), getText(R.string.signup_successful), Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigateUp(); // Go back to SignIn
                binding.btnSignup.setEnabled(true);
                binding.btnSignup.setText(R.string.sign_up);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
            binding.btnSignup.setEnabled(true);
            binding.btnSignup.setText(R.string.sign_up);
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
