package com.eibrahim.winkel.mainActivity;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewItemsWishlist;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WishlistFragment extends Fragment {

    private final List<String> wishlistIds = new ArrayList<>();

    private LinearLayout msgEmptyWishlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        RecyclerView recyclerview_wishlist = rootView.findViewById(R.id.recyclerview_wishlist);

        SwipeRefreshLayout wishlist_fragment = rootView.findViewById(R.id.wishlist_fragment);

        msgEmptyWishlist = rootView.findViewById(R.id.msgEmptyWishlist);


        fetchWishlistData(recyclerview_wishlist, requireContext());


        wishlist_fragment.setOnRefreshListener(() -> {

            fetchWishlistData(recyclerview_wishlist, requireContext());

            wishlist_fragment.setRefreshing(false);

        });

        return rootView;
    }

    private void fetchWishlistData(RecyclerView recyclerView, Context context) {

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
                                        adapterRecyclerviewItemsWishlist adapterRvItems = new adapterRecyclerviewItemsWishlist(context, dataOfRvItems, itemType);
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
                        adapterRecyclerviewItemsWishlist adapterRvItems = new adapterRecyclerviewItemsWishlist(context, dataOfRvItems, null);
                        recyclerView.setAdapter(adapterRvItems);
                    }
                }
            }
        });

    }
}