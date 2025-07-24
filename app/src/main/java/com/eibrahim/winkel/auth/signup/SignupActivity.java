package com.eibrahim.winkel.auth.signup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.eibrahim.winkel.databinding.ActivitySignupBinding;


// No Firebase logic inside Activity anymore.
public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private SignupViewModel viewModel;
    private String userType = "Customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        binding.radioCust.setChecked(true);

        binding.radioAdmin.setOnClickListener(v -> updateUserType("Admin"));
        binding.radioVendor.setOnClickListener(v -> updateUserType("Vendor"));
        binding.radioCust.setOnClickListener(v -> updateUserType("Customer"));

        binding.btnSignup.setOnClickListener(v -> validateAndSignup());
        binding.btnSignin2.setOnClickListener(v -> finish());
    }

    private void updateUserType(String type) {
        binding.radioAdmin.setChecked("Admin".equals(type));
        binding.radioVendor.setChecked("Vendor".equals(type));
        binding.radioCust.setChecked("Customer".equals(type));

        userType = type;
        binding.licenseAdd.setVisibility(View.VISIBLE);
        binding.codeLayout.setVisibility("Admin".equals(type) ? View.VISIBLE : View.GONE);
        binding.codeLayout.setVisibility("Vendor".equals(type) ? View.VISIBLE : View.GONE);
        if ("Customer".equals(type)) binding.licenseAdd.setVisibility(View.GONE);
    }

    private void observeViewModel() {
        viewModel.getSignupSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    private void validateAndSignup() {
        String username = binding.nameSignup.getText().toString().trim();
        String email = binding.emailSignup.getText().toString().trim();
        String password = binding.passSignup.getText().toString();
        String rePassword = binding.repassSignup.getText().toString();
        String pin = binding.pinSignup.getText().toString();
        String repin = binding.rePinSignup.getText().toString();
        String phone = binding.phoneSignup.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty() ||
                pin.isEmpty() || repin.isEmpty() || phone.length() != 11 ||
                !password.equals(rePassword) || !pin.equals(repin) || !binding.checkSignup.isChecked() || !validCode()) {
            Toast.makeText(this, "Please fill valid details.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.createAccount(email, password, username, phone, userType, pin);
    }

    private boolean validCode() {
        String code = binding.codeText.getText().toString().trim();
        if ("Admin".equals(userType)) return "admin44".equals(code);
        if ("Vendor".equals(userType)) return "vendor99".equals(code);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }
}
