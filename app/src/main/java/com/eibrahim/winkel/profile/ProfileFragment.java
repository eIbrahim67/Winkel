package com.eibrahim.winkel.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.auth.AuthActivity;
import com.eibrahim.winkel.databinding.FragmentProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    private final NavOptions navOptionsRight = new NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        navController = NavHostFragment.findNavController(this);

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);

        setupNavigationMap();
        setupLogout();

        return binding.getRoot();
    }

    /**
     * Central navigation mapping — improves performance + readability.
     */
    private void setupNavigationMap() {
        Map<View, Integer> navMap = new HashMap<>();

        navMap.put(binding.btnProfile, R.id.action_profileFragment_to_pinFragment);
        navMap.put(binding.btnLanguages, R.id.action_profileFragment_to_languagesFragment);
        navMap.put(binding.btnMyOrders, R.id.action_profileFragment_to_myOrdersFragment);
        navMap.put(binding.btnHelpFeedback, R.id.action_profileFragment_to_helpFeedbackFragment);
        navMap.put(binding.btnPermissions, R.id.action_profileFragment_to_permissionsFragment);
        navMap.put(binding.btnAbout, R.id.action_profileFragment_to_aboutFragment);
        navMap.put(binding.btnSecurity, R.id.action_profileFragment_to_securityFragment);
        navMap.put(binding.btnDarkMode, R.id.action_profileFragment_to_themeFragment);
        navMap.put(binding.btnAddNewItem, R.id.action_profileFragment_to_addItemFragment);
        navMap.put(binding.btnMyItems, R.id.action_profileFragment_to_myItemsFragment);
        navMap.put(binding.btnDeleteAccount, R.id.action_profileFragment_to_deleteAccountFragment);

        for (Map.Entry<View, Integer> entry : navMap.entrySet()) {
            entry.getKey().setOnClickListener(v ->
                    navController.navigate(entry.getValue(), null, navOptionsRight)
            );
        }
    }

    private void setupLogout() {
        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.log_out)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.yes, (dialog, which) -> logout())
                    .setNegativeButton(R.string.no, null)
                    .show();
        });
    }

    private void logout() {
        try {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(requireContext(), R.string.logged_out_successfully, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            requireActivity().finish();
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bottomNavigationView != null) bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}