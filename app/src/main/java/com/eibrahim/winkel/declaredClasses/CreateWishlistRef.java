package com.eibrahim.winkel.declaredClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateWishlistRef {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

    public void createIt(){

        DocumentReference wishlistRef = firestore.collection("UsersData")
                .document(userId)
                .collection("WishlistCollection")
                .document("wishlistDocument");

        wishlistRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> wishlistData = new HashMap<>();
                    wishlistRef.set(wishlistData)
                            .addOnSuccessListener(aVoid -> {

                            });
                }
            }
        });

    }

}
