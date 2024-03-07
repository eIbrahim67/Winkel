package com.eibrahim.winkel.declaredClasses;

import android.util.Log;

import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddToBasket {


    public void addItemToBasket(DataRecyclerviewItem Item){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = String.valueOf(auth.getCurrentUser().getUid());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("UsersData").
                document(userId).collection("BasketCollection");
        DocumentReference documentRef = collectionRef.document();
        String documentId = documentRef.getId();
        Item.setItemId(documentId);
            collectionRef.document(documentId).set(Item)
        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document added with ID: "))
        .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));

    }

}
