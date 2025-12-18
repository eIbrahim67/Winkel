package com.eibrahim.winkel.auth.forgetPassword;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.FragmentForgetPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordFragment extends Fragment {

    private FragmentForgetPasswordBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnResetPassword.setOnClickListener(v -> {

            binding.btnResetPassword.setEnabled(false);
            binding.btnResetPassword.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);

            String email = binding.emailForgetPassword.getText().toString().trim();

            if (!email.isEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), getText(R.string.password_reset_email_sent_please_check_your_inbox_and_spam_folder), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    binding.btnResetPassword.setEnabled(true);
                    binding.btnResetPassword.setText(R.string.reset_password);
                    binding.progressBar.setVisibility(View.GONE);
                });
            } else {
                Toast.makeText(requireContext(), R.string.please_enter_your_email_address, Toast.LENGTH_SHORT).show();
                binding.btnResetPassword.setEnabled(true);
                binding.btnResetPassword.setText(R.string.reset_password);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }
}