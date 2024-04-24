package com.eibrahim.winkel.declaredClasses;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerviewVisibility {
    LinearLayout main_home_design;
    RelativeLayout tops_view;
    RecyclerView recyclerView_filter;
    public RecyclerviewVisibility(LinearLayout main_home_design, RelativeLayout tops_view, RecyclerView recyclerView_filter){
        this.main_home_design = main_home_design;
        this.tops_view = tops_view;
        this.recyclerView_filter = recyclerView_filter;
    }

    public void recyclerviewVisibility(@NonNull String type){

        switch (type) {

            case "Mens":
            case "Womens":
            case "Kids":
                main_home_design.setVisibility(View.GONE);
                tops_view.setVisibility(View.GONE);
                recyclerView_filter.setVisibility(View.VISIBLE);
                break;
            default:
                main_home_design.setVisibility(View.VISIBLE);
                tops_view.setVisibility(View.VISIBLE);
                recyclerView_filter.setVisibility(View.GONE);
                break;
        }
    }

}
