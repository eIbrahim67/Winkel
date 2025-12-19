package com.eibrahim.winkel.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityThemeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ThemeFragment extends Fragment {

    private ActivityThemeBinding binding;
    private SharedPreferences sharedPreferences;

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
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        try {
            sharedPreferences = requireContext().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
            int isDarkMode = sharedPreferences.getInt("theme_state", -1);
            if (isDarkMode != -1) {
                if (isDarkMode == 1) {
                    binding.RadBtnNight.performClick();
                } else {
                    binding.RadBtnLight.performClick();
                }
            } else {
                binding.RadBtnSystem.performClick();
            }
            binding.btnLight.setOnClickListener(v -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO, 2));
            binding.btnDark.setOnClickListener(v -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES, 1));
            binding.btnSystem.setOnClickListener(v -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, -1));

            binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

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
            Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
