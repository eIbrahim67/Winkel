package com.eibrahim.winkel.deletePages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityDeleteAccountBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DeleteAccountFragment extends Fragment {

    private ActivityDeleteAccountBinding binding;

    public DeleteAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = ActivityDeleteAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    final NavOptions navOptionsRight = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).setLaunchSingleTop(true).setRestoreState(true).build();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        NavController navController = NavHostFragment.findNavController(DeleteAccountFragment.this);
        binding.btnDeleteAccountConfirm.setOnClickListener(v -> {

            navController.navigate(R.id.action_deleteAccountFragment_to_ConfirmDeletion, null, navOptionsRight);

        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
