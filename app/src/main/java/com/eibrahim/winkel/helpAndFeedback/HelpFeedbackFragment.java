package com.eibrahim.winkel.helpAndFeedback;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityHelpFeedbackBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HelpFeedbackFragment extends Fragment {

    private ActivityHelpFeedbackBinding binding;
    private BottomNavigationView bottomNavigationView;

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

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        // If using Navigation component:
        // NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
