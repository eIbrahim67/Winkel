package com.eibrahim.winkel.core;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.loadingindicator.LoadingIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FetchDataFromFirebase {

    private final RecyclerView recyclerView;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String userId;

    private LinearLayout emptyLayout = null;

    private boolean useWishlistAdapter = false;

    public FetchDataFromFirebase(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public FetchDataFromFirebase(RecyclerView recyclerView, Context context, LinearLayout emptyLayout) {
        this(recyclerView, context);
        this.emptyLayout = emptyLayout;
    }

    // ---------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------

    public void fetch(String type) {
        loadWishlistIds(wishlist ->
                loadProducts(type, null, null, null, wishlist)
        );
    }

    public void fetch(String type, String fromPrice, String toPrice) {
        loadWishlistIds(wishlist ->
                loadProducts(type, null, fromPrice, toPrice, wishlist)
        );
    }

    public void fetch(String type, String category, String fromPrice, String toPrice) {
        loadWishlistIds(wishlist ->
                loadProducts(type, category, fromPrice, toPrice, wishlist)
        );
    }

    public void fetchSpecific(String coll, String doc, String type) {
        loadWishlistIds(wishlist ->
                loadSpecific(coll, doc, type, wishlist, null, null)
        );
    }

    public void fetchSpecific(String coll, String doc, String type,
                              LinearLayout emptyLayout, LoadingIndicator loader) {

        useWishlistAdapter = true;
        this.emptyLayout = emptyLayout;

        loadWishlistIds(wishlist -> {
            loadSpecific(coll, doc, type, wishlist, emptyLayout, loader);

            if (wishlist == null || wishlist.isEmpty())
                emptyLayout.setVisibility(View.VISIBLE);
            else
                emptyLayout.setVisibility(View.INVISIBLE);

            loader.setVisibility(View.GONE);
        });
    }

    // ---------------------------------------------------------------------
    // Core Logic
    // ---------------------------------------------------------------------

    private void loadWishlistIds(FetchWishlistCallback callback) {
        db.collection("UsersData")
                .document(userId)
                .collection("Wishlist")
                .document("Wishlist")
                .get()
                .addOnSuccessListener(doc -> {
                    List<String> ids = (List<String>) doc.get("Wishlist");
                    callback.onFetched(ids != null ? ids : new ArrayList<>());
                });
    }

    private void loadProducts(String type, String category, String fPrice, String tPrice,
                              List<String> wishlistIds) {

        db.collection("Products").document(type).collection(type).get()
                .addOnSuccessListener(snapshot -> {

                    List<DataRecyclerviewMyItem> items = snapshot.getDocuments()
                            .stream()
                            .map(doc -> mapToItem(doc, type, wishlistIds))
                            .filter(Objects::nonNull)
                            .filter(item -> filterCategory(item, category))
                            .filter(item -> filterPrice(item, fPrice, tPrice))
                            .collect(Collectors.toList());

                    updateRecycler(items);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading products", e));
    }

    private void loadSpecific(String coll, String doc, String type,
                              List<String> wishlistIds,
                              LinearLayout emptyLayout, LoadingIndicator loader) {

        DocumentReference ref =
                db.collection(coll).document(doc).collection(type).document(type);

        ref.get().addOnSuccessListener(snapshot -> {

            List<String> list = (List<String>) snapshot.get(type);
            if (list == null || list.isEmpty()) {
                updateRecycler(Collections.emptyList());
                if (emptyLayout != null) emptyLayout.setVisibility(View.VISIBLE);
                return;
            }

            // Parse itemId,itemType
            List<DocumentReference> refs = new ArrayList<>();
            for (String entry : list) {
                try {
                    String[] parts = entry.split(",");
                    refs.add(db.collection("Products")
                            .document(parts[1].trim())
                            .collection(parts[1].trim())
                            .document(parts[0].trim()));
                } catch (Exception e) {
                    Toast.makeText(context, "Invalid entry: " + entry, Toast.LENGTH_SHORT).show();
                }
            }

            // Fetch all product docs together
            db.runBatch(batch -> {
                    }) // dummy batch just to attach success listener
                    .addOnSuccessListener(aVoid ->
                            fetchDocuments(refs, wishlistIds, emptyLayout)
                    );
        });
    }

    private void fetchDocuments(List<DocumentReference> refs,
                                List<String> wishlistIds,
                                LinearLayout emptyLayout) {

        List<DataRecyclerviewMyItem> items = new ArrayList<>();

        for (DocumentReference ref : refs) {
            ref.get().addOnSuccessListener(doc -> {
                DataRecyclerviewMyItem item =
                        mapToItem(doc,
                                doc.getReference().getParent().getId(),
                                wishlistIds);
                if (item != null) items.add(item);

                updateRecycler(items);

                if (emptyLayout != null) emptyLayout.setVisibility(View.GONE);
            });
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private DataRecyclerviewMyItem mapToItem(DocumentSnapshot doc, String type,
                                             List<String> wishlistIds) {
        if (doc == null || !doc.exists()) return null;

        Map<String, Object> map = doc.getData();
        if (map == null) return null;

        try {
            String category = (String) map.get("category");
            String imageId = (String) map.get("imageId");
            String name = (String) map.get("name");
            String price = (String) map.get("price");
            String itemId = (String) map.get("itemId");

            DataRecyclerviewMyItem item =
                    new DataRecyclerviewMyItem(category, imageId, name, price, type, "");

            item.setItemId(itemId);
            item.setItemLoved(wishlistIds.contains(itemId + "," + type));

            return item;
        } catch (Exception e) {
            Log.e("MapToItem", "Invalid product data", e);
            return null;
        }
    }

    private boolean filterCategory(DataRecyclerviewMyItem item, String category) {
        return category == null || Objects.equals(item.getCategory(), category);
    }

    private boolean filterPrice(DataRecyclerviewMyItem item,
                                String f, String t) {

        if (f == null || t == null) return true;

        try {
            double price = Double.parseDouble(item.getPrice());
            return price >= Double.parseDouble(f) && price <= Double.parseDouble(t);
        } catch (Exception e) {
            Log.w("PriceFilter", "Invalid price: " + item.getPrice());
            return false;
        }
    }

    private void updateRecycler(List<DataRecyclerviewMyItem> data) {
        if (emptyLayout != null)
            emptyLayout.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);

        RecyclerView.Adapter<?> adapter;

        if (useWishlistAdapter) {
            adapter = new adapterRecyclerviewWishlist(context, data);
        } else {
            adapter = new adapterRecyclerviewItems(context, data);
        }

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setAdapter(adapter);
    }


    interface FetchWishlistCallback {
        void onFetched(List<String> wishlistIds);
    }
}
