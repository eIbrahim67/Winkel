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

import com.eibrahim.winkel.declaredClasses.FetchUserType;
import com.eibrahim.winkel.secondPages.AllUsersActivity;
import com.eibrahim.winkel.secondPages.MyItemsActivity;
import com.eibrahim.winkel.secondPages.MyOrdersActivity;
import com.eibrahim.winkel.secondPages.OrdersActivity;
import com.eibrahim.winkel.secondPages.PinActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.secondPages.SettingsActivity;
import com.eibrahim.winkel.secondPages.SupportActivity;
import com.eibrahim.winkel.secondPages.addPaymentMethodActivity;
import com.eibrahim.winkel.sign.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    LinearLayout btnProfile, btnPaymentMethods, btnOrders, btnLogout, btnAddNewItem, btnMyItems, btnMyOrders, btnSupport, btnSettings, btnAllUsers;

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
         btnSettings = rootView.findViewById(R.id.btnSettings);
         btnSupport = rootView.findViewById(R.id.btnSupport);
         btnMyItems = rootView.findViewById(R.id.btnMyItems);
         btnAllUsers = rootView.findViewById(R.id.btnAllUsers);

        SwipeRefreshLayout profileFragment_layout = rootView.findViewById(R.id.profileFragment_layout);

        FetchUserType fetchUserType = new FetchUserType(btnMyOrders, btnOrders, btnAddNewItem, btnMyItems, btnAllUsers);
        fetchUserType.fetchIt();

         profileFragment_layout.setOnRefreshListener(() -> {

             fetchUserType.fetchIt();

             profileFragment_layout.setRefreshing(false);

         });

         btnAddNewItem.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), addPaymentMethodActivity.class);
            startActivity(intent);

        });

        btnSupport.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), SupportActivity.class);
            startActivity(intent);

        });

        btnSettings.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), SettingsActivity.class);
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

        btnMyItems.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyItemsActivity.class);
            startActivity(intent);
        });

        btnMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
            startActivity(intent);
        });

        btnAllUsers.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllUsersActivity.class);
            startActivity(intent);
        });

         btnLogout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.are_you_sure)
                    .setTitle(R.string.log_out)
                    .setPositiveButton(R.string.yes, (dialog, id) -> {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getActivity(), getText(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show();
                        Intent intentHomeActivity = new Intent(getActivity(), SigninActivity.class);
                        intentHomeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentHomeActivity);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.no, (dialog, id) -> dialog.dismiss());

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });

         return rootView;
    }

}