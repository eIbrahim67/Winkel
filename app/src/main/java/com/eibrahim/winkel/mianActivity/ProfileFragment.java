package com.eibrahim.winkel.mianActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eibrahim.winkel.secondActivity.SecondActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.sign.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout btnProfile = rootView.findViewById(R.id.btnProfile);
        LinearLayout btnPaymentMethods = rootView.findViewById(R.id.btnPaymentMethods);
        LinearLayout btnOrders = rootView.findViewById(R.id.btnOrders);
        LinearLayout btnLogout = rootView.findViewById(R.id.btnLogout);
        LinearLayout btnAddNewItem = rootView.findViewById(R.id.btnAddNewItem);

        btnAddNewItem.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), SecondActivity.class);
            intent.putExtra("state", 3);
            startActivity(intent);

        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SecondActivity.class);
            intent.putExtra("state", 0);
            startActivity(intent);
        });

        btnPaymentMethods.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SecondActivity.class);
            intent.putExtra("state", 1);
            startActivity(intent);
        });

        btnOrders.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SecondActivity.class);
            intent.putExtra("state", 2);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?")
                    .setTitle("Log Out")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getActivity(), "logout successfully", Toast.LENGTH_SHORT).show();
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
}