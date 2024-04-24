package com.eibrahim.winkel.declaredClasses;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

public class DoFilter {

    private RecyclerView recyclerView;
    private Context context;

    public DoFilter(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
    }


    private FetchDataFromFirebase fetchDataFromFirebase;

    public void doFilter(String type, String fPrice, String tPrice, RecyclerView recyclerView_filter){


        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                context
        );

        fetchDataFromFirebase.fetchData(type, fPrice, tPrice);

        FetchCategory.fetchCategory(type, fPrice, tPrice, recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type, RecyclerView recyclerView_filter){


        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                context
        );

        fetchDataFromFirebase.fetchData(type);

        FetchCategory.fetchCategory(type, "0", "1000", recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type){

        FetchDataFromFirebase fetchData = new FetchDataFromFirebase(recyclerView, context);
        fetchData.fetchSpecific("Products", type, type);

    }


}
