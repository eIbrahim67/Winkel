package com.eibrahim.winkel.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.mainPages.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class sortBottomSheet extends BottomSheetDialogFragment {

    private final HomeFragment homeFragment;

    public sortBottomSheet(HomeFragment homeFragment) {

        this.homeFragment = homeFragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.bottom_sheet_sort, container, false);

        ImageView back_sort_btn = root.findViewById(R.id.back_sort_btn);

        back_sort_btn.setOnClickListener(v -> {

            dismiss();

            functionsBottomSheet bottomSheet = new functionsBottomSheet(homeFragment);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });

        return root;
    }

}