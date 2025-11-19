package com.eibrahim.winkel.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityThemeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ThemeFragment extends Fragment {

    private ActivityThemeBinding binding;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;

    public ThemeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityThemeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        try {
            sharedPreferences = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);

            binding.btnLight.setOnClickListener(v -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO, 2));
            binding.btnDark.setOnClickListener(v -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES, 1));
            binding.btnSystem.setOnClickListener(v -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, -1));

            binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
            // Or use: NavHostFragment.findNavController(this).popBackStack();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setThemeMode(int mode, int state) {
        try {
            AppCompatDelegate.setDefaultNightMode(mode);
            sharedPreferences.edit().putInt("theme_state", state).apply();
            requireActivity().recreate();  // Apply the theme change
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
