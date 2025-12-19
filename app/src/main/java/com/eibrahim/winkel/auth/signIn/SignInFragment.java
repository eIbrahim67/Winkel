package com.eibrahim.winkel.auth.signIn;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivitySigninBinding;
import com.eibrahim.winkel.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SignInFragment extends Fragment {

    private ActivitySigninBinding binding;
    private SigninViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivitySigninBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(SigninViewModel.class);

        // Auto-login
        if (viewModel.isUserLoggedIn()) {
            navigateToMain();
        }

        setupListeners();
        observeViewModel();

        return binding.getRoot();
    }

    private final NavOptions navOptionsRight = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).setLaunchSingleTop(true).setRestoreState(true).build();

    private void setupListeners() {
        binding.btnSignin.setOnClickListener(v -> validateAndLogin());

        binding.btnSignup2.setOnClickListener(v -> {
            // Replace this with Navigation component if you use it
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_signupFragment, null, navOptionsRight);
        });

        binding.tvForgetPassword.setOnClickListener(v -> {
            // Replace this with Navigation component if you use it
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_forgetPasswordFragment, null, navOptionsRight);
        });
    }

    private void validateAndLogin() {
        binding.btnSignin.setEnabled(false);
        binding.btnSignin.setText("");
        binding.progressBar.setVisibility(View.VISIBLE);

        String email = binding.emailSignin.getText().toString().trim();
        String password = Objects.requireNonNull(binding.passSignin.getText()).toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Snackbar.make(requireView(), getString(R.string.check_from_your_data), Snackbar.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSignin.setText(getString(R.string.sign_in));
            binding.btnSignin.setEnabled(true);
        } else {
            viewModel.loginUser(email, password);
        }
    }

    private void observeViewModel() {
        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Snackbar.make(requireView(), getString(R.string.authentication_successful), Snackbar.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSignin.setText(getString(R.string.sign_in));
                binding.btnSignin.setEnabled(true);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            Snackbar.make(requireView(), R.string.authentication_failed_check_your_credentials, Snackbar.LENGTH_LONG).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.btnSignin.setText(getString(R.string.sign_in));
            binding.btnSignin.setEnabled(true);
        });
    }

    private void navigateToMain() {
        requireActivity().startActivity(new android.content.Intent(requireContext(), MainActivity.class).setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
