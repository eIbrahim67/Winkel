package com.eibrahim.winkel.declaredClasses;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.adapterClasses. adapterRecyclerviewItems;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.google.android.gms.tasks.OnSuccessListener;
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
    final RecyclerView recyclerViewItemsMens;
    final RecyclerView recyclerViewItemsWomen;
    final RecyclerView recyclerViewItemsKids;
    final RecyclerView recyclerViewItemsOffers;
    final Context context;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();;
    public FetchDataFromFirebase(RecyclerView recyclerViewItems,
                                 RecyclerView recyclerViewItemsMens,
                                 RecyclerView recyclerViewItemsWomen,
                                 RecyclerView recyclerViewItemsKids,
                                 RecyclerView recyclerViewItemsOffers, Context context){
        this.context = context;
        this.recyclerViewItems = recyclerViewItems;
        this.recyclerViewItemsMens = recyclerViewItemsMens;
        this.recyclerViewItemsWomen = recyclerViewItemsWomen;
        this.recyclerViewItemsKids = recyclerViewItemsKids;
        this.recyclerViewItemsOffers = recyclerViewItemsOffers;

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

        if (type.equals("All")){
            fetchData("Mens", fPrice, tPrice, 2, recyclerViewItemsMens);
            fetchData("Womens", fPrice, tPrice, 2, recyclerViewItemsWomen);
            fetchData("Kids", fPrice, tPrice, 2, recyclerViewItemsKids);
            fetchData("Offers", fPrice, tPrice, 2, recyclerViewItemsOffers);
            List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();
            adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(context, dataOfRvItems, type);
            recyclerView.setAdapter(adapterRvItems);
            return;
        }
        else {

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

                            dataObject.setItemId(document.getId());

                            assert wishlistIds != null;
                            dataObject.setItemLoved(wishlistIds.contains(dataObject.getItemId() + "," + dataObject.getItemType()));

                            dataObject.setItemId((String) data.get("itemId"));
                            if(Double.parseDouble(dataObject.getPrice()) >= Double.parseDouble(fPrice) && Double.parseDouble(dataObject.getPrice()) <= Double.parseDouble(tPrice))
                                dataOfRvItems.add(dataObject);
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
        List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();
        adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(context, dataOfRvItems, type);
        recyclerView.setAdapter(adapterRvItems);
        recyclerViewItemsMens.setAdapter(adapterRvItems);
        recyclerViewItemsWomen.setAdapter(adapterRvItems);
        recyclerViewItemsKids.setAdapter(adapterRvItems);
        recyclerViewItemsOffers.setAdapter(adapterRvItems);

    }
}