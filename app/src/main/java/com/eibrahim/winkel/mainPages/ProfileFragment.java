package com.eibrahim.winkel.mainPages;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eibrahim.winkel.secondPages.OrdersActivity;
import com.eibrahim.winkel.secondPages.PinActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.secondPages.addPaymentMethodActivity;
import com.eibrahim.winkel.sign.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    LinearLayout btnProfile, btnPaymentMethods, btnOrders, btnLogout, btnAddNewItem, btnMyOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_profile, container, false);

         btnProfile = rootView.findViewById(R.id.btnProfile);
         btnPaymentMethods = rootView.findViewById(R.id.btnPaymentMethods);
         btnOrders = rootView.findViewById(R.id.btnOrders);
         btnLogout = rootView.findViewById(R.id.btnLogout);
         btnAddNewItem = rootView.findViewById(R.id.btnAddNewItem);
        btnMyOrders = rootView.findViewById(R.id.btnMyOrders);

         SwipeRefreshLayout profileFragment_layout = rootView.findViewById(R.id.profileFragment_layout);

        fetchUserType();

         profileFragment_layout.setOnRefreshListener(() -> {

             fetchUserType();

             profileFragment_layout.setRefreshing(false);

         });

         btnAddNewItem.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), addPaymentMethodActivity.class);
            startActivity(intent);

        });

         btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PinActivity.class);
            intent.putExtra("goto", 0);
            startActivity(intent);
        });

         btnPaymentMethods.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PinActivity.class);
            intent.putExtra("goto", 1);
            startActivity(intent);
        });

         btnOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OrdersActivity.class);
            startActivity(intent);
        });

         btnLogout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?")
                    .setTitle("Log Out")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getActivity(), "Logged out successfully.", Toast.LENGTH_SHORT).show();
                        Intent intentHomeActivity = new Intent(getActivity(), SigninActivity.class);
                        intentHomeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentHomeActivity);
                        dialog.dismiss();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

         return rootView;
    }

    void fetchUserType(){

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
                        }
                        else if (Objects.equals(type, "Vendor")){
                            btnAddNewItem.setVisibility(View.VISIBLE);
                        }
                    }

                });

    }
}