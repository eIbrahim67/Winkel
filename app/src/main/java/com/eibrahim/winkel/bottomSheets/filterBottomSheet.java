package com.eibrahim.winkel.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.DoFilter;
import com.eibrahim.winkel.declaredClasses.RecyclerviewVisibility;
import com.eibrahim.winkel.mainPages.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;


public class filterBottomSheet extends BottomSheetDialogFragment {

    private String type = "NewReleases", fPrice = "0", tPrice = "10000";
    private DoFilter doFilter;

    private final RecyclerView recyclerView_filter;
    private final RecyclerView recyclerView_items;
    private final RecyclerviewVisibility recyclerviewVisibility;
    public filterBottomSheet(RecyclerView recyclerView_filter, RecyclerView recyclerView_items, RecyclerviewVisibility recyclerviewVisibility) {

        this.recyclerView_filter = recyclerView_filter;
        this.recyclerView_items = recyclerView_items;
        this.recyclerviewVisibility = recyclerviewVisibility;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        Button btnFilter = root.findViewById(R.id.btnFilter);

        doFilter = new DoFilter(recyclerView_items,recyclerviewVisibility, requireContext());

        LinearLayout btn_all = root.findViewById(R.id.btn_all);
        LinearLayout btn_men = root.findViewById(R.id.btn_men);
        LinearLayout btn_women = root.findViewById(R.id.btn_women);
        LinearLayout btn_kids = root.findViewById(R.id.btn_kids);

        RadioButton RadBtnAll = root.findViewById(R.id.RadBtnAll);
        RadioButton RadBtnMen = root.findViewById(R.id.RadBtnMen);
        RadioButton RadBtnWomen = root.findViewById(R.id.RadBtnWomen);
        RadioButton RadBtnKid = root.findViewById(R.id.RadBtnKid);

        EditText fPrice = root.findViewById(R.id.fPrice);
        EditText tPrice = root.findViewById(R.id.tPrice);

        ImageView back_filter_btn = root.findViewById(R.id.back_filter_btn);

        back_filter_btn.setOnClickListener(v -> {

            dismiss();

            functionsBottomSheet bottomSheet = new functionsBottomSheet(recyclerView_filter, recyclerView_items, recyclerviewVisibility);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });

        btn_all.setOnClickListener(v -> {
            RadBtnAll.setChecked(true);
            RadBtnMen.setChecked(false);
            RadBtnWomen.setChecked(false);
            RadBtnKid.setChecked(false);
            type = "NewReleases";
        });

        btn_men.setOnClickListener(v -> {
            RadBtnAll.setChecked(false);
            RadBtnMen.setChecked(true);
            RadBtnWomen.setChecked(false);
            RadBtnKid.setChecked(false);
            type = "Mens";
        });

        btn_women.setOnClickListener(v -> {
            RadBtnAll.setChecked(false);
            RadBtnMen.setChecked(false);
            RadBtnWomen.setChecked(true);
            RadBtnKid.setChecked(false);
            type = "Womens";
        });

        btn_kids.setOnClickListener(v -> {
            RadBtnAll.setChecked(false);
            RadBtnMen.setChecked(false);
            RadBtnWomen.setChecked(false);
            RadBtnKid.setChecked(true);
            type = "Kids";
        });

        RadBtnAll.setOnClickListener(v -> btn_all.callOnClick());

        RadBtnMen.setOnClickListener(v -> btn_men.callOnClick());

        RadBtnWomen.setOnClickListener(v -> btn_women.callOnClick());

        RadBtnKid.setOnClickListener(v -> btn_kids.callOnClick());

        btnFilter.setOnClickListener(v -> {

            if (Objects.equals(type, "NewReleases")){

                doFilter.doFilter("NewReleases");
            }
            else {

                Toast.makeText(requireContext() , type, Toast.LENGTH_SHORT).show();
                if (fPrice.getText() == null || fPrice.getText().toString().isEmpty()) {
                    fPrice.setText("0");
                }
                if (tPrice.getText() == null || tPrice.getText().toString().isEmpty()) {
                    tPrice.setText("1000");
                }

                this.fPrice = fPrice.getText().toString();
                this.tPrice = tPrice.getText().toString();

                doFilter.doFilter(type, this.fPrice, this.tPrice, recyclerView_filter);

            }

            dismiss();

        });


        return root;

    }

}