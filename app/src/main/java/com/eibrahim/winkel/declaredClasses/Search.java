package com.eibrahim.winkel.declaredClasses;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewItems;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Search {

    public static void search(Context context, String searchText, RecyclerView recyclerView){


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("Products").document("Mens").collection("Mens");
        collectionRef
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Map<String, Object> data = document.getData();
                        String itemName = (String) (data != null ? data.get("name") : null);

                        // Perform case-insensitive partial string match
                        if (itemName != null &&
                                itemName.toLowerCase().contains(searchText.toLowerCase())) {
                            DataRecyclerviewMyItem dataObject = new DataRecyclerviewMyItem(
                                    (String) data.get("category"),
                                    (String) data.get("imageId"),
                                    itemName,
                                    (String) data.get("price"),
                                    "Mens",
                                    ""
                            );

                            dataObject.setItemId((String) data.get("itemId"));
                            dataObject.setItemLoved(false);

                            dataOfRvItems.add(dataObject);
                        }
                    }


                    adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(
                            context,
                            dataOfRvItems);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(adapterRvItems);


                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error getting documents", e));


    }
}
