package com.eibrahim.winkel.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.RecyclerviewVisibility;
import com.eibrahim.winkel.mainPages.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class viewBottomSheet extends BottomSheetDialogFragment {

    private final RecyclerView recyclerView_filter;

    private final RecyclerView recyclerView_items;

    private final RecyclerviewVisibility recyclerviewVisibility;
    public viewBottomSheet(RecyclerView recyclerView_filter, RecyclerView recyclerView_items, RecyclerviewVisibility recyclerviewVisibility) {

        this.recyclerView_filter = recyclerView_filter;
        this.recyclerView_items = recyclerView_items;
        this.recyclerviewVisibility = recyclerviewVisibility;

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.bottom_sheet_view, container, false);

        ImageView back_view_btn = root.findViewById(R.id.back_view_btn);

        back_view_btn.setOnClickListener(v -> {

            dismiss();

            functionsBottomSheet bottomSheet = new functionsBottomSheet(recyclerView_filter, recyclerView_items, recyclerviewVisibility);
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });


        return root;

    }

}