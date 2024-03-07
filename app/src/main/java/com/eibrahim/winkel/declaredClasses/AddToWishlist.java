package com.eibrahim.winkel.declaredClasses;

import android.util.Log;

import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddToWishlist {


    public void addItemToBasket(DataRecyclerviewItem Item){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = String.valueOf(auth.getCurrentUser().getUid());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

// Assuming Item is an object representing the data you want to store
        DocumentReference documentRef = firestore.collection("UsersData")
                .document(userId)
                .collection("WishlistCollection")
                .document(String.valueOf(Item.getItemId()));
        documentRef.set(Item)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document added with ID: "))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));

    }


}
