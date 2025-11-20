package com.eibrahim.winkel.deletePages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.auth.AuthActivity;
import com.eibrahim.winkel.databinding.ActivityConfirmDeletionBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ConfirmDeletionFragment extends Fragment {

    private ActivityConfirmDeletionBinding binding;
    private BottomNavigationView bottomNavigationView;
    private DocumentReference pinRef;

    public ConfirmDeletionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityConfirmDeletionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        try {

            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
            pinRef = FirebaseFirestore.getInstance().collection("UsersData").document(userId).collection("UserPersonalData").document("UserPersonalData");

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Initialization Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }

        binding.btnDeleteAccountFinal.setOnClickListener(v -> {

            String code = binding.deleteAccountPin.getText().toString().trim();

            if (!code.isEmpty()) {

                pinRef.get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String correctPin = String.valueOf(snapshot.get("pin"));
                        if (code.equals(correctPin)) {

                            new AlertDialog.Builder(requireContext()).setTitle(R.string.delete_account).setMessage(R.string.are_you_sure_you_want_to_permanently_delete_your_account).setPositiveButton(R.string.yes, (dialog, which) -> deleteMyAccount()).setNegativeButton(R.string.cancel, null).show();


                        } else {
                            Toast.makeText(requireContext(), getText(R.string.incorrect_pin_please_try_again), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), getText(R.string.pin_not_found_please_contact_support), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(requireContext(), getText(R.string.failed_to_verify_pin_please_check_your_connection), Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(requireContext(), getString(R.string.please_enter_your_admin_code), Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void logout() {
        try {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(requireContext(), getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMyAccount() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        // 1. Delete Firestore data
        DocumentReference userDataRef = firestore.collection("UsersData").document(uid).collection("UserPersonalData").document("UserPersonalData");

        userDataRef.delete().addOnSuccessListener(aVoid -> {
            // 2. Try deleting Firebase Authentication account
            auth.getCurrentUser().delete().addOnSuccessListener(aVoid1 -> {
                Toast.makeText(requireContext(), R.string.account_deleted_successfully, Toast.LENGTH_SHORT).show();
                logout(); // Redirect to AuthActivity
            }).addOnFailureListener(e -> {
                if (e.getMessage() != null && e.getMessage().contains("recent login")) {
                    Toast.makeText(requireContext(), R.string.please_re_login_before_deleting_your_account, Toast.LENGTH_LONG).show();
                    logout(); // Optionally force logout so user logs in again
                } else {
                    Toast.makeText(requireContext(), "Auth deletion failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
