package com.eibrahim.winkel.home.bottomSheet;

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
import com.eibrahim.winkel.core.DoFilter;
import com.eibrahim.winkel.core.RecyclerviewVisibility;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class filterBottomSheet extends BottomSheetDialogFragment {

    private String type = "NewReleases";
    private String fromPrice = "0";
    private String toPrice = "1000";

    private final RecyclerView recyclerViewFilter;
    private final RecyclerView recyclerViewItems;
    private final RecyclerviewVisibility recyclerviewVisibility;
    private final DoFilter doFilter;
    private final LinearLayout skeletonLayout;

    public filterBottomSheet(RecyclerView recyclerViewFilter,
                             RecyclerView recyclerViewItems,
                             RecyclerviewVisibility recyclerviewVisibility,
                             DoFilter doFilter,
                             LinearLayout skeletonLayout) {

        this.recyclerViewFilter = recyclerViewFilter;
        this.recyclerViewItems = recyclerViewItems;
        this.recyclerviewVisibility = recyclerviewVisibility;
        this.doFilter = doFilter;
        this.skeletonLayout = skeletonLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        Button btnFilter = root.findViewById(R.id.btnFilter);

        LinearLayout btnAll = root.findViewById(R.id.btn_all);
        LinearLayout btnMen = root.findViewById(R.id.btn_men);
        LinearLayout btnWomen = root.findViewById(R.id.btn_women);
        LinearLayout btnKids = root.findViewById(R.id.btn_kids);

        RadioButton rbAll = root.findViewById(R.id.RadBtnAll);
        RadioButton rbMen = root.findViewById(R.id.RadBtnMen);
        RadioButton rbWomen = root.findViewById(R.id.RadBtnWomen);
        RadioButton rbKids = root.findViewById(R.id.RadBtnKid);

        EditText etFromPrice = root.findViewById(R.id.fPrice);
        EditText etToPrice = root.findViewById(R.id.tPrice);

        ImageView backBtn = root.findViewById(R.id.back_filter_btn);

        backBtn.setOnClickListener(v -> {
            dismiss();
            new functionsBottomSheet(
                    recyclerViewFilter,
                    recyclerViewItems,
                    recyclerviewVisibility,
                    doFilter, skeletonLayout
            ).show(requireActivity().getSupportFragmentManager(), "functions_sheet");
        });

        btnAll.setOnClickListener(v -> selectType("NewReleases", rbAll, rbMen, rbWomen, rbKids));
        btnMen.setOnClickListener(v -> selectType("Mens", rbMen, rbAll, rbWomen, rbKids));
        btnWomen.setOnClickListener(v -> selectType("Womens", rbWomen, rbAll, rbMen, rbKids));
        btnKids.setOnClickListener(v -> selectType("Kids", rbKids, rbAll, rbMen, rbWomen));

        rbAll.setOnClickListener(v -> btnAll.callOnClick());
        rbMen.setOnClickListener(v -> btnMen.callOnClick());
        rbWomen.setOnClickListener(v -> btnWomen.callOnClick());
        rbKids.setOnClickListener(v -> btnKids.callOnClick());

        btnFilter.setOnClickListener(v -> {

            normalizePriceInputs(etFromPrice, etToPrice);

            if (Objects.equals(type, "NewReleases")) {
                doFilter.doFilter("NewReleases", skeletonLayout);
            } else {
                doFilter.doFilter(
                        type,
                        fromPrice,
                        toPrice,
                        recyclerViewFilter, skeletonLayout
                );
            }

            dismiss();
        });

        return root;
    }

    /* -------------------------------------------------- */
    /* Helpers                                            */
    /* -------------------------------------------------- */

    private void selectType(String selected,
                            RadioButton checked,
                            RadioButton... others) {

        type = selected;
        checked.setChecked(true);
        for (RadioButton rb : others) rb.setChecked(false);
    }

    private void normalizePriceInputs(EditText from, EditText to) {

        fromPrice = from.getText() == null || from.getText().toString().isEmpty()
                ? "0"
                : from.getText().toString();

        toPrice = to.getText() == null || to.getText().toString().isEmpty()
                ? "1000"
                : to.getText().toString();
    }
}
