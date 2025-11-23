package com.eibrahim.winkel.helpAndFeedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityHelpFeedbackBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class HelpFeedbackFragment extends Fragment {

    private ActivityHelpFeedbackBinding binding;

    public HelpFeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityHelpFeedbackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide bottom navigation safely
        BottomNavigationView bottomNavigationView =
                requireActivity().findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }

        // Back button
        binding.btnBack.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        binding.btnSend.setOnClickListener(v -> {
            Snackbar.make(requireView(),
                    R.string.your_feedback_has_been_send_successfully,
                    Snackbar.LENGTH_SHORT).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}