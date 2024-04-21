package com.eibrahim.winkel.declaredClasses;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.adapterClasses. adapterRecyclerviewItems;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FetchDataFromFirebase {

    final RecyclerView recyclerViewItems;
    final Context context;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

    public FetchDataFromFirebase(RecyclerView recyclerViewItems, Context context){
        this.context = context;
        this.recyclerViewItems = recyclerViewItems;

    }



    public void fetchFilterData(String filter, String type, String fPrice, String tPrice) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();

        CollectionReference collectionRef = firestore.collection("Products").document(type).collection(type);
        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Get the document data
                        Map<String, Object> data = document.getData();

                        DataRecyclerviewMyItem dataObject = new DataRecyclerviewMyItem(
                                (String) Objects.requireNonNull(data).get("category"),
                                (String) data.get("imageId"),
                                (String) data.get("name"),
                                (String) data.get("price"),
                                type,
                                ""
                        );

                        //dataObject.setItemLoved(wishlistIds.contains(dataObject.getItemId()));

                        if (Objects.equals(dataObject.getCategory(), filter))
                            if(Double.parseDouble(dataObject.getPrice()) >= Double.parseDouble(fPrice) && Double.parseDouble(dataObject.getPrice()) <= Double.parseDouble(tPrice))
                                dataOfRvItems.add(dataObject);
                    }

                    adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(context, dataOfRvItems, type);
                    recyclerViewItems.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerViewItems.setAdapter(adapterRvItems);
                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error getting documents", e));
    }

    public void fetchData(String type, String fPrice, String tPrice, int stateShow, RecyclerView recyclerView) {

        firestore
                .collection("UsersData").document(userId)
                .collection("WishlistCollection").document("wishlistDocument")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()){

                        List<String> wishlistIds = (List<String>) documentSnapshot.get("WishlistCollection");
                        fetch(type, fPrice, tPrice, stateShow, recyclerView, wishlistIds);
                    }

                });

    }

    private void fetch(String type, String fPrice, String tPrice, int stateShow, RecyclerView recyclerView, List<String> wishlistIds) {


            List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference collectionRef = firestore.collection("Products").document(type).collection(type);
            collectionRef
                    .get()
                    .addOnSuccessListener(querySnapshot -> {

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                            Map<String, Object> data = document.getData();
                            DataRecyclerviewMyItem dataObject = new DataRecyclerviewMyItem(
                                    (String) Objects.requireNonNull(data).get("category"),
                                    (String) data.get("imageId"),
                                    (String) data.get("name"),
                                    (String) data.get("price"),
                                    type,
                                    ""
                            );

                            dataObject.setItemId((String) data.get("itemId"));


                            if (wishlistIds != null)
                                dataObject.setItemLoved(wishlistIds.contains(dataObject.getItemId() + "," + dataObject.getItemType()));


                            if(Double.parseDouble(dataObject.getPrice()) >= Double.parseDouble(fPrice) && Double.parseDouble(dataObject.getPrice()) <= Double.parseDouble(tPrice))
                                dataOfRvItems.add(dataObject);

                            if (stateShow == 2){
                                if (dataOfRvItems.size() == 4)
                                    break;
                            }

                        }
                        adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(context, dataOfRvItems, type);
                        if(stateShow == 1)
                            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                        else if(stateShow == 2)
                            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

                        recyclerView.setAdapter(adapterRvItems);
                    })
                    .addOnFailureListener(e -> Log.d("Firestore", "Error getting documents", e));


    }
}