package com.eibrahim.winkel.personalData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityPersonalDataBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PersonalDataFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private ActivityPersonalDataBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String userType = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityPersonalDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
        binding.btnBack.setOnClickListener(v -> navController.popBackStack(R.id.profileFragment, false));

        loadUserData();

        binding.saveMyNewName.setOnClickListener(v -> updateField(getString(R.string.user_name), binding.updateName.getText().toString().trim()));
        binding.saveMyNewPhone.setOnClickListener(v -> updateField(getString(R.string.phone_number), binding.newPhone.getText().toString().trim()));

        binding.sendToBeAnAdmin.setOnClickListener(v -> {
            String code = binding.codeToBeAdmin.getText().toString().trim();

            if (!code.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.your_request_has_been_submitted_successfully), Toast.LENGTH_SHORT).show();
                // Optional: Save the request to Firestore or notify the administrator
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_enter_your_admin_code), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void loadUserData() {
        String uid = auth.getCurrentUser().getUid();
        DocumentReference userRef = firestore.collection("UsersData").document(uid).collection("UserPersonalData").document("UserPersonalData");

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.getString("userName");
                String phone = snapshot.getString("phoneNo");
                String email = auth.getCurrentUser().getEmail();
                userType = snapshot.getString("userType");

                binding.updateName.setText(name != null ? name : "");
                binding.newPhone.setText(phone != null ? phone : "");
                binding.emailText.setText(email != null ? email : "");


                if ("Admin".equalsIgnoreCase(userType)) {
                    Toast.makeText(getContext(), "You are Admin", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateField(String fieldName, String value) {
        if (value.isEmpty()) {
            Toast.makeText(getContext(), "Please enter valid " + fieldName, Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        DocumentReference userRef = firestore.collection("UsersData").document(uid).collection("UserPersonalData").document("UserPersonalData");

        Map<String, Object> updates = new HashMap<>();
        updates.put(fieldName, value);

        userRef.update(updates).addOnSuccessListener(aVoid -> Toast.makeText(getContext(), fieldName + getString(R.string.updated), Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
