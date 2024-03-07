package com.eibrahim.winkel.mianActivity;

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
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewItems;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WishlistFragment extends Fragment {

    public static final List<String> wishlistIds = new ArrayList<>();

    private int items = 0;
    private LinearLayout msgEmptyWishlist;


    public WishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);
        RecyclerView recyclerview_wishlist = rootView.findViewById(R.id.recyclerview_wishlist);
        SwipeRefreshLayout wishlist_fragment = rootView.findViewById(R.id.wishlist_fragment);
        msgEmptyWishlist = rootView.findViewById(R.id.msgEmptyWishlist);
        fetchWishlistData(recyclerview_wishlist, requireContext());

        wishlist_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fetchWishlistData(recyclerview_wishlist, requireContext());

                wishlist_fragment.setRefreshing(false);

            }
        });

        return rootView;
    }

    private void fetchWishlistData(RecyclerView recyclerView, Context context) {

        items = 0;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = String.valueOf(auth.getCurrentUser().getUid());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<DataRecyclerviewItem> dataOfRvItems = new ArrayList<>();

        CollectionReference collectionRef = firestore.collection("UsersData")
                .document(userId).collection("WishlistCollection");
        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        Map<String, Object> data = document.getData();

                        DataRecyclerviewItem dataObject = new DataRecyclerviewItem(
                                (String) data.get("category"),
                                (String) data.get("imageId"),
                                (String) data.get("name"),
                                (String) data.get("price")
                        );
                        items++;
                        String documentId = document.getId();
                        wishlistIds.add(documentId);

                        dataObject.setItemId((String) data.get("itemId"));
                        dataOfRvItems.add(dataObject);
                    }

                    adapterRecyclerviewItems adapterRvItems = new adapterRecyclerviewItems(context, dataOfRvItems, 2);
                    adapterRvItems.wishlistFragment = WishlistFragment.this;
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerView.setAdapter(adapterRvItems);
                    checkEmptyWishlist(0);
                })
                .addOnFailureListener(e -> {
                });

    }
    public void checkEmptyWishlist(int state){
        items += state;
        if (items == 0)
            msgEmptyWishlist.setVisibility(View.VISIBLE);
    }
}