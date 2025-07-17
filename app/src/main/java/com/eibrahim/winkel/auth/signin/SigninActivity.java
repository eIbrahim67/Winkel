package com.eibrahim.winkel.auth.signin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eibrahim.winkel.auth.signin.SigninViewModel;
import com.eibrahim.winkel.auth.signup.SignupActivity;
import com.eibrahim.winkel.databinding.ActivitySigninBinding;
import com.eibrahim.winkel.mainPages.MainActivity;

public class SigninActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private SigninViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SigninViewModel.class);

        // Auto-login check
        if (viewModel.isUserLoggedIn()) {
            navigateToMain();
        }

        binding.btnSignin.setOnClickListener(v -> validateAndLogin());
        binding.btnSignup2.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));

        observeViewModel();
    }

    private void validateAndLogin() {
        String email = binding.emailSignin.getText().toString().trim();
        String password = binding.passSignin.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(com.eibrahim.winkel.R.string.check_from_your_data), Toast.LENGTH_SHORT).show();
        } else {
            viewModel.loginUser(email, password);
        }
    }

    private void observeViewModel() {
        viewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, getString(com.eibrahim.winkel.R.string.authentication_successful), Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
