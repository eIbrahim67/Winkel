package com.eibrahim.winkel.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.DoFilter;
import com.eibrahim.winkel.declaredClasses.RecyclerviewVisibility;
import com.eibrahim.winkel.mainPages.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class functionsBottomSheet extends BottomSheetDialogFragment {

    private final RecyclerView recyclerView_filter;

    private final RecyclerView recyclerView_items;
    private final RecyclerviewVisibility recyclerviewVisibility;
    private final DoFilter doFilter;
    public functionsBottomSheet(RecyclerView recyclerView_filter, RecyclerView recyclerView_items, RecyclerviewVisibility recyclerviewVisibility, DoFilter doFilter) {

        this.recyclerView_filter = recyclerView_filter;
        this.recyclerView_items = recyclerView_items;
        this.recyclerviewVisibility = recyclerviewVisibility;
        this.doFilter = doFilter;
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

            filterBottomSheet bottomSheet = new filterBottomSheet(recyclerView_filter, recyclerView_items, recyclerviewVisibility, doFilter);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });

        sort_btn.setOnClickListener(v -> {

            dismiss();

            sortBottomSheet bottomSheet = new sortBottomSheet(recyclerView_filter, recyclerView_items, recyclerviewVisibility, doFilter);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });

        view_btn.setOnClickListener(v -> {

            dismiss();

            viewBottomSheet bottomSheet = new viewBottomSheet(recyclerView_filter, recyclerView_items, recyclerviewVisibility, doFilter);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });


        return root0;

    }


    

}