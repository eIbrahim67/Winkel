package com.eibrahim.winkel.core;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private final LinearLayout skeletonLayout;
    private LinearLayout emptyLayout;

    private boolean useWishlistAdapter = false;

    public FetchDataFromFirebase(RecyclerView recyclerView,
                                 Context context,
                                 LinearLayout skeletonLayout) {
        this.userId = Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()
        ).getUid();
        this.recyclerView = recyclerView;
        this.context = context;
        this.skeletonLayout = skeletonLayout;
    }

    /* ------------------------------------------------------------------ */
    /* Public API                                                         */
    /* ------------------------------------------------------------------ */

    public void fetch(String type) {
        startLoading();
        loadWishlistIds(wishlist ->
                loadProducts(type, null, null, null, wishlist)
        );
    }

    public void fetch(String type, String fromPrice, String toPrice) {
        startLoading();
        loadWishlistIds(wishlist ->
                loadProducts(type, null, fromPrice, toPrice, wishlist)
        );
    }

    public void fetch(String type, String category, String fromPrice, String toPrice) {
        startLoading();
        loadWishlistIds(wishlist ->
                loadProducts(type, category, fromPrice, toPrice, wishlist)
        );
    }

    public void fetchSpecific(String coll, String doc, String type) {
        startLoading();
        loadWishlistIds(wishlist ->
                loadSpecific(coll, doc, type, wishlist)
        );
    }

    public void fetchSpecific(String coll,
                              String doc,
                              String type,
                              LinearLayout emptyLayout) {

        useWishlistAdapter = true;
        this.emptyLayout = emptyLayout;
        startLoading();

        loadWishlistIds(wishlist ->
                loadSpecific(coll, doc, type, wishlist)
        );
    }

    /* ------------------------------------------------------------------ */
    /* Core Logic                                                          */
    /* ------------------------------------------------------------------ */

    private void startLoading() {
        recyclerView.setAdapter(null);

        if (skeletonLayout != null) {
            skeletonLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        if (emptyLayout != null) {
            emptyLayout.setVisibility(View.GONE);
        }
    }

    private void stopLoading() {
        if (skeletonLayout != null) {
            skeletonLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

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

    private void loadProducts(String type,
                              String category,
                              String fPrice,
                              String tPrice,
                              List<String> wishlistIds) {

        db.collection("Products")
                .document(type)
                .collection(type)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<DataRecyclerviewMyItem> items =
                            snapshot.getDocuments()
                                    .stream()
                                    .map(doc -> mapToItem(doc, type, wishlistIds))
                                    .filter(Objects::nonNull)
                                    .filter(item -> filterCategory(item, category))
                                    .filter(item -> filterPrice(item, fPrice, tPrice))
                                    .collect(Collectors.toList());

                    updateRecycler(items);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error loading products", e);
                    updateRecycler(Collections.emptyList());
                });
    }

    private void loadSpecific(String coll,
                              String doc,
                              String type,
                              List<String> wishlistIds) {

        DocumentReference ref =
                db.collection(coll)
                        .document(doc)
                        .collection(type)
                        .document(type);

        ref.get().addOnSuccessListener(snapshot -> {

            List<String> list = (List<String>) snapshot.get(type);

            if (list == null || list.isEmpty()) {
                updateRecycler(Collections.emptyList());
                return;
            }

            List<DocumentReference> refs = new ArrayList<>();

            for (String entry : list) {
                try {
                    String[] parts = entry.split(",");
                    refs.add(
                            db.collection("Products")
                                    .document(parts[1].trim())
                                    .collection(parts[1].trim())
                                    .document(parts[0].trim())
                    );
                } catch (Exception e) {
                    Log.e("ParseError", "Invalid entry: " + entry);
                }
            }

            fetchDocuments(refs, wishlistIds);
        });
    }

    private void fetchDocuments(List<DocumentReference> refs,
                                List<String> wishlistIds) {

        List<DataRecyclerviewMyItem> items = new ArrayList<>();
        final int total = refs.size();

        for (DocumentReference ref : refs) {
            ref.get().addOnSuccessListener(doc -> {
                DataRecyclerviewMyItem item =
                        mapToItem(
                                doc,
                                doc.getReference().getParent().getId(),
                                wishlistIds
                        );

                if (item != null) items.add(item);

                if (items.size() == total) {
                    updateRecycler(items);
                }
            });
        }
    }

    /* ------------------------------------------------------------------ */
    /* Helpers                                                             */
    /* ------------------------------------------------------------------ */

    private DataRecyclerviewMyItem mapToItem(DocumentSnapshot doc,
                                             String type,
                                             List<String> wishlistIds) {

        if (doc == null || !doc.exists()) return null;

        Map<String, Object> map = doc.getData();
        if (map == null) return null;

        try {
            DataRecyclerviewMyItem item =
                    new DataRecyclerviewMyItem(
                            (String) map.get("category"),
                            (String) map.get("imageId"),
                            (String) map.get("name"),
                            (String) map.get("price"),
                            type,
                            ""
                    );

            String itemId = (String) map.get("itemId");
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

    private boolean filterPrice(DataRecyclerviewMyItem item, String f, String t) {
        if (f == null || t == null) return true;

        try {
            double price = Double.parseDouble(item.getPrice());
            return price >= Double.parseDouble(f)
                    && price <= Double.parseDouble(t);
        } catch (Exception e) {
            return false;
        }
    }

    private void updateRecycler(List<DataRecyclerviewMyItem> data) {

        stopLoading();

        if (emptyLayout != null) {
            emptyLayout.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        }

        RecyclerView.Adapter<?> adapter =
                useWishlistAdapter
                        ? new adapterRecyclerviewWishlist(context, data)
                        : new adapterRecyclerviewItems(context, data);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setAdapter(adapter);
    }

    interface FetchWishlistCallback {
        void onFetched(List<String> wishlistIds);
    }
}
