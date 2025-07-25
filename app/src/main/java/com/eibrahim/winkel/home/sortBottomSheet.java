package com.eibrahim.winkel.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.DoFilter;
import com.eibrahim.winkel.declaredClasses.RecyclerviewVisibility;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class sortBottomSheet extends BottomSheetDialogFragment {

    private final RecyclerView recyclerView_filter;

    private final RecyclerView recyclerView_items;
    private final RecyclerviewVisibility recyclerviewVisibility;
    private final DoFilter doFilter;

    public sortBottomSheet(RecyclerView recyclerView_filter, RecyclerView recyclerView_items, RecyclerviewVisibility recyclerviewVisibility, DoFilter doFilter) {

        this.recyclerView_filter = recyclerView_filter;
        this.recyclerView_items = recyclerView_items;
        this.recyclerviewVisibility = recyclerviewVisibility;
        this.doFilter = doFilter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.bottom_sheet_sort, container, false);

        ImageView back_sort_btn = root.findViewById(R.id.back_sort_btn);

        back_sort_btn.setOnClickListener(v -> {

            dismiss();

            functionsBottomSheet bottomSheet = new functionsBottomSheet(recyclerView_filter, recyclerView_items, recyclerviewVisibility, doFilter);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });

        return root;
    }

}