package com.eibrahim.winkel.languages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityLanguagesBinding;
import com.eibrahim.winkel.main.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class LanguagesFragment extends Fragment {

    private ActivityLanguagesBinding binding;
    private BottomNavigationView bottomNavigationView;

    public LanguagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityLanguagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnSystem.setOnClickListener(v -> {
            SharedPreferences.Editor editor = requireActivity()
                    .getSharedPreferences("Settings", getContext().MODE_PRIVATE)
                    .edit();
            editor.remove("My_Lang").apply();

            showAlertDialog();
        });

        binding.btnEnglish.setOnClickListener(v -> setLocale("en"));
        binding.btnArabic.setOnClickListener(v -> setLocale("ar"));
    }

    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        requireActivity().getResources().updateConfiguration(config,
                requireActivity().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = requireActivity()
                .getSharedPreferences("Settings", getContext().MODE_PRIVATE)
                .edit();
        editor.putString("My_Lang", languageCode);
        editor.apply();

        showAlertDialog();
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Restart Required")
                .setMessage("The application needs to restart to apply the changes.")
                .setPositiveButton("Restart", (dialog, which) -> restartApp())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void restartApp() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finishAffinity();  // Close all previous activities
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
