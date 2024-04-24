package com.eibrahim.winkel.declaredClasses;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.mainPages.HomeFragment;

import java.util.Objects;

public class DoFilter {

    private final RecyclerView recyclerView;
    private final Context context;
    private final RecyclerviewVisibility recyclerviewVisibility;
    private FetchDataFromFirebase fetchDataFromFirebase;
    private int action = 0;
    String type,  fPrice, tPrice;
    RecyclerView recyclerView_filter;
    public DoFilter(RecyclerView recyclerView, RecyclerviewVisibility recyclerviewVisibility, Context context) {
        this.recyclerView = recyclerView;
        this.recyclerviewVisibility = recyclerviewVisibility;
        this.context = context;
    }

    public void doFilter(String type, String fPrice, String tPrice, RecyclerView recyclerView_filter){
        action = 1;
        this.type = type;
        this.fPrice = fPrice;
        this.tPrice = tPrice;
        this.recyclerView_filter = recyclerView_filter;

        recyclerviewVisibility.recyclerviewVisibility(type);
        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                context
        );

        fetchDataFromFirebase.fetchData(type, fPrice, tPrice);

        FetchCategory.fetchCategory(type, fPrice, tPrice, recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type, RecyclerView recyclerView_filter){
        action = 2;
        this.type = type;
        this.recyclerView_filter = recyclerView_filter;

        recyclerviewVisibility.recyclerviewVisibility(type);
        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                context
        );

        fetchDataFromFirebase.fetchData(type);

        FetchCategory.fetchCategory(type, "0", "1000", recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type){

        action = 0;
        this.type = type;
        recyclerviewVisibility.recyclerviewVisibility(type);
        FetchDataFromFirebase fetchData = new FetchDataFromFirebase(recyclerView, context);
        fetchData.fetchSpecific("Products", type, type);

    }
    public void lastAction(){

        switch (action){
            case 0:{
                doFilter(type);
                break;
            }
            case 1:{
                doFilter(type, fPrice, tPrice, recyclerView_filter);
                break;
            }
            case 2:{
                doFilter(type, recyclerView_filter);
                break;
            }

        }
    }

}
