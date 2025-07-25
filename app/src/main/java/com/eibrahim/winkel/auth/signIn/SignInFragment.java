package com.eibrahim.winkel.auth.signIn;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivitySigninBinding;
import com.eibrahim.winkel.main.MainActivity;

public class SignInFragment extends Fragment {

    private ActivitySigninBinding binding;
    private SigninViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    private void setupListeners() {
        binding.btnSignin.setOnClickListener(v -> validateAndLogin());

        binding.btnSignup2.setOnClickListener(v -> {
            // Replace this with Navigation component if you use it
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_signupFragment);
        });

        binding.tvForgetPassword.setOnClickListener(v -> {
            // Replace this with Navigation component if you use it
            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_forgetPasswordFragment);
        });
    }

    private void validateAndLogin() {
        String email = binding.emailSignin.getText().toString().trim();
        String password = binding.passSignin.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), getString(R.string.check_from_your_data), Toast.LENGTH_SHORT).show();
        } else {
            viewModel.loginUser(email, password);
        }
    }

    private void observeViewModel() {
        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), getString(R.string.authentication_successful), Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
        });
    }

    private void navigateToMain() {
        requireActivity().startActivity(new android.content.Intent(requireContext(), MainActivity.class)
                .setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
