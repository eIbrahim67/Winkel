package com.eibrahim.winkel.declaredClasses;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewItemsWishlist;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FetchSpecificData {

    public final RecyclerView recyclerView; // Public field declaration
    public final Context context; // Public field declaration
    //public final List<String> wishlistIds; // Public field declaration

    public FetchSpecificData(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
    }

    public void fetchIt(String coll,String doc, String type) {

        List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference wishlistRef = firestore.collection(coll)
                .document(doc)
                .collection(type)
                .document(type);

        wishlistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> wishlist = (List<String>) documentSnapshot.get(type);
                if (wishlist != null) {


                    if (wishlist.size() > 0){

                        for (String itemIdType : wishlist) {

                            String[] parts = itemIdType.split(",");
                            String itemId = parts[0].trim();
                            String itemType = parts[1].trim();

                            DocumentReference documentRef = firestore.collection("Products")
                                    .document(itemType)
                                    .collection(itemType)
                                    .document(itemId);

                            documentRef.get().addOnSuccessListener(querySnapshot  -> {
                                if (querySnapshot .exists()) {
                                    Map<String, Object> data = querySnapshot .getData();
                                    if (data != null) {
                                        String category = (String) data.get("category");
                                        String imageId = (String) data.get("imageId");
                                        String name = (String) data.get("name");
                                        String price = (String) data.get("price");
                                        DataRecyclerviewMyItem dataObject = new DataRecyclerviewMyItem(
                                                category,
                                                imageId,
                                                name,
                                                price,
                                                itemType,
                                                ""
                                        );
                                        dataObject.setItemId(itemId);
                                        dataOfRvItems.add(dataObject);
                                        adapterRecyclerviewItemsWishlist adapterRvItems = new adapterRecyclerviewItemsWishlist(context, dataOfRvItems);
                                        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                                        recyclerView.setAdapter(adapterRvItems);
                                    }
                                }
                            });
                        }

                    }
                    else{
                        dataOfRvItems.clear();
                        adapterRecyclerviewItemsWishlist adapterRvItems = new adapterRecyclerviewItemsWishlist(context, dataOfRvItems);
                        recyclerView.setAdapter(adapterRvItems);
                    }
                }
            }
        });

    }

}
