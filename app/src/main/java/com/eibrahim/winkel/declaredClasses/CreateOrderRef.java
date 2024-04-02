package com.eibrahim.winkel.declaredClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateOrderRef {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

    public void createIt(){

        DocumentReference ordersRef = firestore.collection("Orders")
                .document(userId);
        ordersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> basketData = new HashMap<>();
                    ordersRef.set(basketData)
                            .addOnSuccessListener(aVoid -> {});
                }
            }
        });

    }

}
