package com.eibrahim.winkel.languages;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityLanguagesBinding;
import com.eibrahim.winkel.main.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class LanguagesFragment extends Fragment {

    private ActivityLanguagesBinding binding;

    public LanguagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityLanguagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences prefs = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");
        updateSelection(language);


        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnEnglish.setOnClickListener(v -> setLocale("en"));
        binding.btnArabic.setOnClickListener(v -> setLocale("ar"));
    }

    public void setLocale(String languageCode) {
        new AlertDialog.Builder(requireContext()).setTitle(R.string.restart_required).setMessage(R.string.the_application_needs_to_restart_to_apply_the_changes).setPositiveButton(R.string.restart, (dialog, which) -> {

            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);
            requireActivity().getResources().updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());

            getContext();
            SharedPreferences.Editor editor = requireActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
            editor.putString("My_Lang", languageCode);
            editor.apply();

            restartApp();
        }).setNegativeButton(R.string.cancel, null).show();
    }

    private void restartApp() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finishAffinity();  // Close all previous activities
    }

    private void updateSelection(String state) {
        if (state != null) {
            binding.btnEnglish.setSelected(state.equals("en"));
            binding.btnArabic.setSelected(state.equals("ar"));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
