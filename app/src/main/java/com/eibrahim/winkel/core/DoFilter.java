package com.eibrahim.winkel.core;

import android.view.View;
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
    private final RecyclerviewVisibility recyclerviewVisibility;

    private FetchDataFromFirebase fetchDataFromFirebase;

    private int action = 0;
    private String type, fPrice, tPrice;
    private RecyclerView recyclerViewFilter;
    private LinearLayout skeletonLayout;

    public DoFilter(RecyclerView recyclerView,
                    RecyclerviewVisibility recyclerviewVisibility) {

        this.recyclerView = recyclerView;
        this.recyclerviewVisibility = recyclerviewVisibility;
    }

    /* -------------------- Public API -------------------- */

    public void doFilter(String type, String fPrice, String tPrice, RecyclerView filterRv, LinearLayout skeleton) {
        action = 1;
        saveState(type, fPrice, tPrice, filterRv, skeleton);
        startFiltering();

        fetchDataFromFirebase.fetch(type, fPrice, tPrice);
        fetchCategory(type, fPrice, tPrice);
    }

    public void doFilter(String type, RecyclerView filterRv, LinearLayout skeleton) {
        action = 2;
        saveState(type, "0", "1000", filterRv, skeleton);
        startFiltering();

        fetchDataFromFirebase.fetch(type);
        fetchCategory(type, "0", "1000");
    }

    public void doFilter(String type, LinearLayout skeleton) {
        action = 0;
        saveState(type, null, null, null, skeleton);
        startFiltering();

        fetchDataFromFirebase.fetchSpecific("Products", type, type);
    }

    public void lastAction() {
        switch (action) {
            case 0:
                doFilter(type, skeletonLayout);
                break;

            case 1:
                doFilter(type, fPrice, tPrice, recyclerViewFilter, skeletonLayout);
                break;

            case 2:
                doFilter(type, recyclerViewFilter, skeletonLayout);
                break;
        }
    }

    /* -------------------- Core Logic -------------------- */

    private void startFiltering() {
        recyclerviewVisibility.recyclerviewVisibility(type);
        clearRecyclerView();
        showSkeleton();

        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView,
                skeletonLayout
        );
    }

    private void clearRecyclerView() {
        recyclerView.setAdapter(null);
    }

    private void showSkeleton() {
        if (skeletonLayout != null) {
            skeletonLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void hideSkeleton() {
        if (skeletonLayout != null) {
            skeletonLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void saveState(String type,
                           String fPrice,
                           String tPrice,
                           RecyclerView filterRv,
                           LinearLayout skeleton) {

        this.type = type;
        this.fPrice = fPrice;
        this.tPrice = tPrice;
        this.recyclerViewFilter = filterRv;
        this.skeletonLayout = skeleton;
    }

    /* -------------------- Category Fetch -------------------- */

    private void fetchCategory(String type, String fPrice, String tPrice) {

        if (recyclerViewFilter == null) {
            hideSkeleton();
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<String> categories = new ArrayList<>();

        recyclerViewFilter.setAdapter(null);

        DocumentReference ref =
                firestore.collection("Data").document("Categories" + type);

        ref.get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        List<Object> data =
                                (List<Object>) snapshot.get("Categories");

                        for (Object item : Objects.requireNonNull(data)) {
                            categories.add(item.toString());
                        }
                    }

                    adapterRecyclerviewCategories adapter =
                            new adapterRecyclerviewCategories(
                                    categories,
                                    recyclerView,
                                    type,
                                    fPrice,
                                    tPrice,
                                    skeletonLayout
                            );

                    recyclerViewFilter.setLayoutManager(
                            new LinearLayoutManager(recyclerView.getContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false)
                    );

                    recyclerViewFilter.setAdapter(adapter);
                    hideSkeleton();
                })
                .addOnFailureListener(e -> hideSkeleton());
    }
}
