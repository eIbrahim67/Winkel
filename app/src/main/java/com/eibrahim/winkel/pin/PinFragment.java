package com.eibrahim.winkel.pin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityPinBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PinFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;


    private ActivityPinBinding binding;
    private String pin = "";
    private boolean wrong = false;
    private DocumentReference pinRef;

    public PinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityPinBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        try {

            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
            pinRef = FirebaseFirestore.getInstance().collection("UsersData").document(userId).collection("UserPersonalData").document("UserPersonalData");

            setClickListeners();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Initialization Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }
    }

    private void setClickListeners() {
        binding.btnBackPin.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnPin00.setOnClickListener(v -> {
            binding.btnPin0.callOnClick();
            if (!wrong) binding.btnPin0.callOnClick();
        });

        binding.btnPin0.setOnClickListener(v -> onDigitClick("0"));
        binding.btnPin1.setOnClickListener(v -> onDigitClick("1"));
        binding.btnPin2.setOnClickListener(v -> onDigitClick("2"));
        binding.btnPin3.setOnClickListener(v -> onDigitClick("3"));
        binding.btnPin4.setOnClickListener(v -> onDigitClick("4"));
        binding.btnPin5.setOnClickListener(v -> onDigitClick("5"));
        binding.btnPin6.setOnClickListener(v -> onDigitClick("6"));
        binding.btnPin7.setOnClickListener(v -> onDigitClick("7"));
        binding.btnPin8.setOnClickListener(v -> onDigitClick("8"));
        binding.btnPin9.setOnClickListener(v -> onDigitClick("9"));

        binding.btnPinDelete.setOnClickListener(v -> onDelete());
    }

    private void onDigitClick(String digit) {
        try {
            pin += digit;

            if (wrong) resetPinIndicators();

            updatePinIndicator();

            if (pin.length() == 4) validatePin();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            pin = "";
            resetPinIndicators();
        }
    }

    private void resetPinIndicators() {
        wrong = false;
        setIndicatorState(0, R.drawable.rounded_gray_v1_gray_v12_none, 0);
        setIndicatorState(1, R.drawable.rounded_gray_v1_gray_v12_none, 0);
        setIndicatorState(2, R.drawable.rounded_gray_v1_gray_v12_none, 0);
        setIndicatorState(3, R.drawable.rounded_gray_v1_gray_v12_none, 0);
    }

    private void updatePinIndicator() {
        int size = pin.length();
        if (size >= 1)
            setIndicatorState(0, R.drawable.rounded_white_v1_blue_v1_gray_v4, R.drawable.star_icon_blue);
        if (size >= 2)
            setIndicatorState(1, R.drawable.rounded_white_v1_blue_v1_gray_v4, R.drawable.star_icon_blue);
        if (size >= 3)
            setIndicatorState(2, R.drawable.rounded_white_v1_blue_v1_gray_v4, R.drawable.star_icon_blue);
        if (size == 4)
            setIndicatorState(3, R.drawable.rounded_white_v1_blue_v1_gray_v4, R.drawable.star_icon_blue);
    }

    private void setIndicatorState(int index, int background, int icon) {
        switch (index) {
            case 0:
                binding.pinP1.setBackgroundResource(background);
                binding.pinP1.setImageResource(icon);
                break;
            case 1:
                binding.pinP2.setBackgroundResource(background);
                binding.pinP2.setImageResource(icon);
                break;
            case 2:
                binding.pinP3.setBackgroundResource(background);
                binding.pinP3.setImageResource(icon);
                break;
            case 3:
                binding.pinP4.setBackgroundResource(background);
                binding.pinP4.setImageResource(icon);
                break;
        }
    }

    private void validatePin() {
        pinRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String correctPin = String.valueOf(snapshot.get("pin"));
                if (pin.equals(correctPin)) {
                    onPinSuccess();
                } else {
                    onPinFailure();
                }
            } else {
                Toast.makeText(requireContext(), "PIN not set for user.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to fetch PIN", Toast.LENGTH_SHORT).show());
    }

    NavOptions navOptionsRight = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_right).setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).setLaunchSingleTop(true).setRestoreState(true).build();

    private void onPinSuccess() {
        setAllIndicators(R.drawable.rounded_white_v1_green_v1_gray_v4, R.drawable.star_icon_green);

        NavController navController = NavHostFragment.findNavController(PinFragment.this);

        navController.navigate(R.id.action_pinFragment_to_personalDataFragment, null, navOptionsRight);

    }

    private void onPinFailure() {
        wrong = true;
        setAllIndicators(R.drawable.rounded_white_v1_red_v1_gray_v4, R.drawable.star_icon_red);
        Toast.makeText(requireContext(), getString(R.string.wrong_pin_message), Toast.LENGTH_SHORT).show();
        pin = "";
    }

    private void setAllIndicators(int background, int icon) {
        setIndicatorState(0, background, icon);
        setIndicatorState(1, background, icon);
        setIndicatorState(2, background, icon);
        setIndicatorState(3, background, icon);
    }

    private void onDelete() {
        if (pin.length() > 0) {
            setIndicatorState(pin.length() - 1, R.drawable.rounded_gray_v1_gray_v12_none, 0);
            pin = pin.substring(0, pin.length() - 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
