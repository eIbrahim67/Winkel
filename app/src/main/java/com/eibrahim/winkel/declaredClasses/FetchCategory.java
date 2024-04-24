package com.eibrahim.winkel.declaredClasses;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewCategories;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FetchCategory {
    public static void fetchCategory(String type, String fPrice, String tPrice, RecyclerView recyclerView_filter, RecyclerView recyclerView_items, Context context){

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<String> dataOfRvFilter = new ArrayList<>();

        DocumentReference docReference = firestore.collection("Data").document("Categories" + type);

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
