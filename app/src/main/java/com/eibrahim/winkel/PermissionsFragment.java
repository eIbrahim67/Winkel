package com.eibrahim.winkel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.databinding.ActivityPermissionsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PermissionsFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;

    private ActivityPermissionsBinding binding;

    public PermissionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityPermissionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        // Optional: replace with Navigation popBackStack if you're using Navigation Component
        // NavHostFragment.findNavController(this).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
