package com.eibrahim.winkel.declaredClasses;

import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class FetchUserType {

    public final LinearLayout for_admins; // Public field declaration
    public final LinearLayout for_vendors; // Public field declaration

    public FetchUserType(LinearLayout for_admins, LinearLayout for_vendors) {
        this.for_admins = for_admins;
        this.for_vendors = for_vendors;
    }

    public void fetchIt() {

                            for_vendors.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("UsersData")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("UserPersonalData")
                .document("UserPersonalData")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String type = (String) documentSnapshot.get("userType");

                    if (documentSnapshot.exists()){

                        if (Objects.equals(type, "Admin")){
                            for_admins.setVisibility(View.VISIBLE);
                        }
                        else if (Objects.equals(type, "Vendor")){
                        }
                    }

                });

    }
}
