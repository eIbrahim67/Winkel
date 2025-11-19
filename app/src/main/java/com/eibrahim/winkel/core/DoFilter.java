package com.eibrahim.winkel.core;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoFilter {

    private final RecyclerView recyclerView;
    private final Context context;
    private final RecyclerviewVisibility recyclerviewVisibility;
    private FetchDataFromFirebase fetchDataFromFirebase;
    private int action = 0;
    String type,  fPrice, tPrice;
    RecyclerView recyclerView_filter;
    private LinearLayout skeleton_layout;

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

        fetchCategory(type, fPrice, tPrice, recyclerView_filter, recyclerView, context);

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

        fetchCategory(type, "0", "1000", recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type, RecyclerView recyclerView_filter, LinearLayout layout){
        action = 2;
        this.type = type;
        this.recyclerView_filter = recyclerView_filter;
        this.skeleton_layout = layout;
        recyclerviewVisibility.recyclerviewVisibility(type);
        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                context,
                layout
        );

        fetchDataFromFirebase.fetchData(type);

        fetchCategory(type, "0", "1000", recyclerView_filter, recyclerView, context);

    }

    public void doFilter(String type){

        action = 0;
        this.type = type;
        recyclerviewVisibility.recyclerviewVisibility(type);
        FetchDataFromFirebase fetchData = new FetchDataFromFirebase(recyclerView, context);
        fetchData.fetchSpecific("Products", type, type);

    }

    public void doFilter(String type, LinearLayout layout){
        action = 0;
        this.type = type;
        this.skeleton_layout = layout;
        recyclerviewVisibility.recyclerviewVisibility(type);
        FetchDataFromFirebase fetchData = new FetchDataFromFirebase(recyclerView, context, layout);
        fetchData.fetchSpecific("Products", type, type);

    }

    public void lastAction(){

        switch (action){
            case 0:{
                if (skeleton_layout != null)
                    doFilter(type, skeleton_layout);
                else
                    doFilter(type);
                break;
            }
            case 1:{
                doFilter(type, fPrice, tPrice, recyclerView_filter);
                break;
            }
            case 2:{
                if (skeleton_layout != null)
                    doFilter(type, recyclerView_filter, skeleton_layout);
                else
                    doFilter(type, recyclerView_filter);
                break;
            }
        }
    }

    public static void fetchCategory(String type, String fPrice, String tPrice, RecyclerView recyclerView_filter, RecyclerView recyclerView_items, Context context){

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<String> dataOfRvFilter = new ArrayList<>();

        DocumentReference docReference = firestore.collection("Data").document("Categories" + type);

        recyclerView_filter.setAdapter(null);

        docReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Object> data = (List<Object>) documentSnapshot.get("Categories");
                for (Object item : Objects.requireNonNull(data)) {
                    dataOfRvFilter.add(item.toString());
                }
            }
            adapterRecyclerviewCategories adapterRvFilter = new adapterRecyclerviewCategories(dataOfRvFilter, recyclerView_items, type,fPrice, tPrice,context);
            recyclerView_filter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            recyclerView_filter.setAdapter(adapterRvFilter);
        }).addOnFailureListener(e -> {
        });
    }

}
