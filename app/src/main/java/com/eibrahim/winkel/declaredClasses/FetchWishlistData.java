package com.eibrahim.winkel.declaredClasses;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

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

public class FetchWishlistData {

    public final RecyclerView recyclerView; // Public field declaration
    public final Context context; // Public field declaration
    public final List<String> wishlistIds; // Public field declaration
    public final LinearLayout msgEmptyWishlist; // Public field declaration

    public FetchWishlistData(RecyclerView recyclerView, Context context, List<String> wishlistIds, LinearLayout msgEmptyWishlist) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.wishlistIds = wishlistIds;
        this.msgEmptyWishlist = msgEmptyWishlist;
    }

    public void fetchIt() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference wishlistRef = firestore.collection("UsersData")
                .document(userId)
                .collection("WishlistCollection")
                .document("wishlistDocument");

        wishlistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> wishlist = (List<String>) documentSnapshot.get("WishlistCollection");
                if (wishlist != null) {

                    if (wishlist.size() > 0){
                        msgEmptyWishlist.setVisibility(View.GONE);

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
                                        wishlistIds.add(itemId);
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
                        msgEmptyWishlist.setVisibility(View.VISIBLE);
                        dataOfRvItems.clear();
                        adapterRecyclerviewItemsWishlist adapterRvItems = new adapterRecyclerviewItemsWishlist(context, dataOfRvItems);
                        recyclerView.setAdapter(adapterRvItems);
                    }
                }
            }
        });

    }

}
