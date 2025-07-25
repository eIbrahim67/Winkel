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
import androidx.navigation.fragment.NavHostFragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.auth.AuthActivity;
import com.eibrahim.winkel.auth.signIn.SignInFragment;
import com.eibrahim.winkel.databinding.FragmentProfileBinding;
import com.eibrahim.winkel.declaredClasses.FetchUserType;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FetchUserType fetchUserType;
    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        try {
            fetchUserType = new FetchUserType(binding.forAdmin);
            fetchUserType.fetchIt();

            binding.profileFragmentLayout.setOnRefreshListener(() -> {
                fetchUserType.fetchIt();
                binding.profileFragmentLayout.setRefreshing(false);
            });

            setUpNavigation();

            binding.btnLogout.setOnClickListener(v -> showLogoutDialog());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
        }

        return binding.getRoot();
    }

    private void setUpNavigation() {
//        binding.btnPaymentMethods.setOnClickListener(v -> navigateTo(PinFragment.class, "goto", 1));
//        binding.btnSupport.setOnClickListener(v -> navigateTo(SupportUsActivity.class));
//        binding.btnSound.setOnClickListener(v -> navigateTo(SoundActivity.class));
//        binding.btnNotifications.setOnClickListener(v -> navigateTo(NotificationsActivity.class));
//        binding.btnAllUsers.setOnClickListener(v -> navigateTo(AllUsersActivity.class));

        NavController navController = NavHostFragment.findNavController(ProfileFragment.this);

        binding.btnProfile.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_pinFragment);
        });

        binding.btnLanguages.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_languagesFragment);
        });


        binding.btnMyOrders.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_myOrdersFragment);
        });

        binding.btnHelpFeedback.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_helpFeedbackFragment);
        });

        binding.btnPermissions.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_permissionsFragment);
        });

        binding.btnAbout.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_aboutFragment);
        });

        binding.btnSecurity.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_securityFragment);
        });

        binding.btnDarkMode.setOnClickListener(v -> {
            navController.navigate(R.id.action_profileFragment_to_themeFragment);
        });


        binding.btnAddNewItem.setOnClickListener(v -> {

            navController.navigate(R.id.action_profileFragment_to_addItemFragment);

        });

        binding.btnMyItems.setOnClickListener(v -> {

            navController.navigate(R.id.action_profileFragment_to_myItemsFragment);

        });

        binding.btnDeleteAccount.setOnClickListener(v -> {

            navController.navigate(R.id.action_profileFragment_to_deleteAccountFragment);


        });


//        binding.btnOrders.setOnClickListener(v -> navigateTo(OrdersFragment.class));

    }

    private void showLogoutDialog() {
        if (getActivity() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.log_out)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(getText(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    logout();
                })
                .setNegativeButton(getText(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();

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

    @Override
    public void onResume() {
        super.onResume();
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
