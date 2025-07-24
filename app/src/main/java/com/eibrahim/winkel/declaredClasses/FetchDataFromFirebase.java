package com.eibrahim.winkel.declaredClasses;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.adapterClasses. adapterRecyclerviewItems;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FetchDataFromFirebase {

    final RecyclerView recyclerView;
    final Context context;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

    private LinearLayout layout = null;

    public FetchDataFromFirebase(RecyclerView recyclerView, Context context){
        this.context = context;
        this.recyclerView = recyclerView;
        recyclerView.setAdapter(null);

    }

    public FetchDataFromFirebase(RecyclerView recyclerView, Context context, LinearLayout layout){
        this.context = context;
        this.recyclerView = recyclerView;
        this.layout = layout;

        layout.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(null);

    }

    public void fetchData(String type, String category, String fPrice, String tPrice) {//

        firestore
                .collection("UsersData").document(userId)
                .collection("Wishlist").document("Wishlist")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()){

                        List<String> wishlistIds = (List<String>) documentSnapshot.get("Wishlist");
                        fetch(type,category, fPrice, tPrice, wishlistIds);
                    }

                });

    }
    private void fetch( String type, String category, String fPrice, String tPrice, List<String> wishlistIds) {
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

                        if (wishlistIds != null)
                            dataObject.setItemLoved(wishlistIds.contains(dataObject.getItemId() + "," + dataObject.getItemType()));

                        if (Objects.equals(dataObject.getCategory(), category))
                            if(Double.parseDouble(dataObject.getPrice()) >= Double.parseDouble(fPrice) && Double.parseDouble(dataObject.getPrice()) <= Double.parseDouble(tPrice))
                                dataOfRvItems.add(dataObject);
                    }

                    adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(context, dataOfRvItems);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerView.setAdapter(adapterRvItems);

                    if(layout != null)
                        layout.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error getting documents", e));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void fetchData(String type, String fPrice, String tPrice) {

        firestore
                .collection("UsersData").document(userId)
                .collection("Wishlist").document("Wishlist")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()){

                        List<String> wishlistIds = (List<String>) documentSnapshot.get("Wishlist");
                        fetch(type, fPrice, tPrice, wishlistIds);
                    }

                });

    }
    private void fetch(String type, String fPrice, String tPrice, List<String> wishlistIds) {


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


                        try {
                            String priceStr = dataObject.getPrice();
                            if (priceStr != null && !priceStr.trim().isEmpty()) {
                                double price = Double.parseDouble(priceStr.trim());
                                double from = Double.parseDouble(fPrice.trim());
                                double to = Double.parseDouble(tPrice.trim());

                                if (price >= from && price <= to) {
                                    dataOfRvItems.add(dataObject);
                                }
                            }
                        } catch (NumberFormatException e) {
                            Log.w("PriceParseError", "Invalid price in document: " + dataObject.getPrice(), e);
                        }

                    }
                    adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(context, dataOfRvItems);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerView.setAdapter(adapterRvItems);

                    if(layout != null)
                        layout.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error getting documents", e));

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void fetchData(String type) {

        firestore
                .collection("UsersData").document(userId)
                .collection("Wishlist").document("Wishlist")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()){

                        List<String> wishlistIds = (List<String>) documentSnapshot.get("Wishlist");
                        fetch(type, wishlistIds);
                    }

                });

    }
    private void fetch(String type, List<String> wishlistIds) {


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


                        dataOfRvItems.add(dataObject);


                    }
                    adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(context, dataOfRvItems);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    recyclerView.setAdapter(adapterRvItems);

                    if(layout != null)
                        layout.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.d("Firestore", "Error getting documents", e));


    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void fetchSpecific(String coll, String doc, String type) {

        firestore
                .collection("UsersData").document(userId)
                .collection("Wishlist").document("Wishlist")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()){

                        List<String> wishlistIds = (List<String>) documentSnapshot.get("Wishlist");
                        fetchSpecific(coll, doc, type, wishlistIds);
                    }

                });

    }

    public void fetchSpecific(String coll, String doc, String type, LinearLayout layout) {

        firestore
                .collection("UsersData").document(userId)
                .collection("Wishlist").document("Wishlist")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()){

                        List<String> wishlistIds = (List<String>) documentSnapshot.get("Wishlist");
                        fetchSpecific(coll, doc, type, wishlistIds);

                        if (wishlistIds != null && wishlistIds.isEmpty())
                            layout.setVisibility(View.VISIBLE);
                        else
                            layout.setVisibility(View.INVISIBLE);

                    }
                    else
                        layout.setVisibility(View.VISIBLE);

                });

    }

    private void fetchSpecific(String coll, String doc, String type, List<String> dataListIds) {

        List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        DocumentReference wishlistRef = firestore.collection(coll)
                .document(doc)
                .collection(type)
                .document(type);

        wishlistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> dataList = (List<String>) documentSnapshot.get(type);
                if (dataList != null) {

                    if (!dataList.isEmpty()){

                        for (String itemIdType : dataList) {

                            try {
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

                                            if (dataListIds != null)
                                                dataObject.setItemLoved(dataListIds.contains(dataObject.getItemId() + "," + dataObject.getItemType()));

                                            dataOfRvItems.add(dataObject);
                                            adapterRecyclerviewItems adapterRvItems = new adapterRecyclerviewItems(context, dataOfRvItems);
                                            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                                            recyclerView.setAdapter(adapterRvItems);

                                            if(layout != null)
                                                layout.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            catch (Exception e){

                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();

                            }


                        }

                    }
                    else{
                        dataOfRvItems.clear();
                        adapterRecyclerviewItems adapterRvItems = new adapterRecyclerviewItems(context, dataOfRvItems);
                        recyclerView.setAdapter(adapterRvItems);

                        if(layout != null)
                            layout.setVisibility(View.GONE);
                    }
                }
            }
        });

    }
}