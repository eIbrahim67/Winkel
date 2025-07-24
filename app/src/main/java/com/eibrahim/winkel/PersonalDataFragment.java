package com.eibrahim.winkel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.databinding.ActivityPersonalDataBinding;

public class PersonalDataFragment extends Fragment {

    private ActivityPersonalDataBinding binding;

    public PersonalDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityPersonalDataBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(PersonalDataFragment.this);
        binding.btnBack.setOnClickListener(v ->
                navController.popBackStack(R.id.profileFragment, false)
        );


        // If you have any buttons or actions, you can handle them here
    }
}