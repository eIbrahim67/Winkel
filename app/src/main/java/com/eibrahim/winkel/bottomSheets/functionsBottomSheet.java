package com.eibrahim.winkel.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.mainActivity.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class functionsBottomSheet extends BottomSheetDialogFragment {

    private String type = "All", fPrice = "0", tPrice = "10000";

    private final HomeFragment homeFragment;

    public functionsBottomSheet(HomeFragment homeFragment) {

        this.homeFragment = homeFragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View root0 = inflater.inflate(R.layout.bottom_sheet_functions, container, false);

        RelativeLayout sort_btn = root0.findViewById(R.id.sort_btn);
        RelativeLayout filter_btn = root0.findViewById(R.id.filter_btn);
        RelativeLayout view_btn = root0.findViewById(R.id.view_btn);


        filter_btn.setOnClickListener(v -> {

            dismiss();

            filterBottomSheet bottomSheet = new filterBottomSheet(homeFragment);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });

        sort_btn.setOnClickListener(v -> {

            dismiss();

            sortBottomSheet bottomSheet = new sortBottomSheet(homeFragment);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });


        return root0;

    }


    

}