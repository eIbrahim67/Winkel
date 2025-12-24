package com.eibrahim.winkel.auth.signup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
        String rePin = binding.rePinSignup.getText().toString();
        String phone = binding.phoneSignup.getText().toString();

        if (TextUtils.isEmpty(username)) {
            showError(R.string.error_username_required, binding.nameSignup);

        } else if (TextUtils.isEmpty(email)) {
            showError(R.string.error_email_required, binding.emailSignup);

        } else if (TextUtils.isEmpty(password)) {
            showError(R.string.error_password_required, binding.passSignup);

        } else if (TextUtils.isEmpty(rePassword)) {
            showError(R.string.error_confirm_password_required, binding.repassSignup);

        } else if (!password.equals(rePassword)) {
            showError(R.string.error_passwords_not_match, binding.repassSignup);

        } else if (TextUtils.isEmpty(pin)) {
            showError(R.string.error_pin_required, binding.pinSignup);

        } else if (TextUtils.isEmpty(rePin)) {
            showError(R.string.error_confirm_pin_required, binding.rePinSignup);

        } else if (!pin.equals(rePin)) {
            showError(R.string.error_pins_not_match, binding.rePinSignup);

        } else if (phone.length() != 11) {
            showError(R.string.error_phone_invalid, binding.phoneSignup);

        } else if (!binding.checkSignup.isChecked()) {
            showError(R.string.error_terms_required, binding.checkSignup);

        } else {
            viewModel.createAccount(email, password, username, phone, pin);
        }
    }

    private void showError(int messageRes, View focusView) {
        Snackbar.make(requireView(), getString(messageRes), Snackbar.LENGTH_SHORT).show();

        binding.btnSignup.setEnabled(true);
        binding.btnSignup.setText(R.string.sign_up);
        binding.progressBar.setVisibility(View.GONE);

        focusView.requestFocus();

        if (focusView instanceof EditText) {
            ((EditText) focusView).setSelection(
                    ((EditText) focusView).getText().length()
            );
        }
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
