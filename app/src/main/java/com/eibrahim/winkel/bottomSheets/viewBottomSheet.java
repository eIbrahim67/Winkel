package com.eibrahim.winkel.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.mainActivity.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class viewBottomSheet extends BottomSheetDialogFragment {

    private final HomeFragment homeFragment;

    public viewBottomSheet(HomeFragment homeFragment) {

        this.homeFragment = homeFragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.bottom_sheet_view, container, false);

        ImageView back_view_btn = root.findViewById(R.id.back_view_btn);

        back_view_btn.setOnClickListener(v -> {

            dismiss();

            functionsBottomSheet bottomSheet = new functionsBottomSheet(homeFragment);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });











//        RelativeLayout btn_all = root.findViewById(R.id.btn_all);
//        RelativeLayout btn_men = root.findViewById(R.id.btn_men);
//        RelativeLayout btn_women = root.findViewById(R.id.btn_women);
//        RelativeLayout btn_kids = root.findViewById(R.id.btn_kids);
//
//        RadioButton RadBtnAll = root.findViewById(R.id.RadBtnAll);
//        RadioButton RadBtnMen = root.findViewById(R.id.RadBtnMen);
//        RadioButton RadBtnWomen = root.findViewById(R.id.RadBtnWomen);
//        RadioButton RadBtnKid = root.findViewById(R.id.RadBtnKid);
//
//        btn_all.setOnClickListener(v -> {
//            RadBtnAll.setChecked(true);
//            RadBtnMen.setChecked(false);
//            RadBtnWomen.setChecked(false);
//            RadBtnKid.setChecked(false);
//            type = "All";
//        });
//
//        btn_men.setOnClickListener(v -> {
//            RadBtnAll.setChecked(false);
//            RadBtnMen.setChecked(true);
//            RadBtnWomen.setChecked(false);
//            RadBtnKid.setChecked(false);
//            type = "Mens";
//        });
//
//        btn_women.setOnClickListener(v -> {
//            RadBtnAll.setChecked(false);
//            RadBtnMen.setChecked(false);
//            RadBtnWomen.setChecked(true);
//            RadBtnKid.setChecked(false);
//            type = "Womens";
//        });
//
//        btn_kids.setOnClickListener(v -> {
//            RadBtnAll.setChecked(false);
//            RadBtnMen.setChecked(false);
//            RadBtnWomen.setChecked(false);
//            RadBtnKid.setChecked(true);
//            type = "Kids";
//        });
//
//        RadBtnAll.setOnClickListener(v -> btn_all.callOnClick());
//
//        RadBtnMen.setOnClickListener(v -> btn_men.callOnClick());
//
//        RadBtnWomen.setOnClickListener(v -> btn_women.callOnClick());
//
//        RadBtnKid.setOnClickListener(v -> btn_kids.callOnClick());


        return root;

    }

}