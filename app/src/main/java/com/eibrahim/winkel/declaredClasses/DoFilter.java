package com.eibrahim.winkel.declaredClasses;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.mainPages.HomeFragment;

public class DoFilter {

    private RecyclerView recyclerView;
    private Context context;

    private RecyclerviewVisibility recyclerviewVisibility;
    public DoFilter(RecyclerView recyclerView, RecyclerviewVisibility recyclerviewVisibility, Context context) {
        this.recyclerView = recyclerView;
        this.recyclerviewVisibility = recyclerviewVisibility;
        this.context = context;
    }


    private FetchDataFromFirebase fetchDataFromFirebase;

    public void doFilter(String type, String fPrice, String tPrice, RecyclerView recyclerView_filter){

        recyclerviewVisibility.recyclerviewVisibility(type);
        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                context
        );

        fetchDataFromFirebase.fetchData(type, fPrice, tPrice);

        FetchCategory.fetchCategory(type, fPrice, tPrice, recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type, RecyclerView recyclerView_filter){

        recyclerviewVisibility.recyclerviewVisibility(type);
        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                context
        );

        fetchDataFromFirebase.fetchData(type);

        FetchCategory.fetchCategory(type, "0", "1000", recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type){
        recyclerviewVisibility.recyclerviewVisibility(type);
        FetchDataFromFirebase fetchData = new FetchDataFromFirebase(recyclerView, context);
        fetchData.fetchSpecific("Products", type, type);

    }


}
