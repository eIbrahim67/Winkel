package com.eibrahim.winkel.declaredClasses;

import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class FetchUserType {

    public final LinearLayout btnMyOrders; // Public field declaration
    public final LinearLayout btnOrders; // Public field declaration
    public final LinearLayout btnAddNewItem; // Public field declaration
    public final LinearLayout btnMyItems; // Public field declaration
    public final LinearLayout btnAllUsers; // Public field declaration

    public FetchUserType(LinearLayout btnMyOrders, LinearLayout btnOrders, LinearLayout btnAddNewItem, LinearLayout btnMyItems, LinearLayout btnAllUsers) {
        this.btnMyOrders = btnMyOrders;
        this.btnOrders = btnOrders;
        this.btnAddNewItem = btnAddNewItem;
        this.btnMyItems = btnMyItems;
        this.btnAllUsers = btnAllUsers;
    }

    public void fetchIt() {

        FirebaseFirestore.getInstance().collection("UsersData")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("UserPersonalData")
                .document("UserPersonalData")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String type = (String) documentSnapshot.get("userType");

                    if (documentSnapshot.exists()){

                        if (Objects.equals(type, "Customer")){
                            btnMyOrders.setVisibility(View.VISIBLE);
                        }
                        else if (Objects.equals(type, "Admin")){
                            btnOrders.setVisibility(View.VISIBLE);
                            btnAllUsers.setVisibility(View.VISIBLE);
                        }
                        else if (Objects.equals(type, "Vendor")){
                            btnAddNewItem.setVisibility(View.VISIBLE);
                            btnMyItems.setVisibility(View.VISIBLE);
                        }
                    }

                });

    }
}
