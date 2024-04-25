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
import com.eibrahim.winkel.secondPages.AboutActivity;
import com.eibrahim.winkel.secondPages.AllUsersActivity;
import com.eibrahim.winkel.secondPages.DeleteAccountActivity;
import com.eibrahim.winkel.secondPages.EventsActivity;
import com.eibrahim.winkel.secondPages.HelpFeedbackActivity;
import com.eibrahim.winkel.secondPages.LanguagesActivity;
import com.eibrahim.winkel.secondPages.MyItemsActivity;
import com.eibrahim.winkel.secondPages.MyOrdersActivity;
import com.eibrahim.winkel.secondPages.NotificationsActivity;
import com.eibrahim.winkel.secondPages.OrdersActivity;
import com.eibrahim.winkel.secondPages.PermissionsActivity;
import com.eibrahim.winkel.secondPages.PinActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.secondPages.SecurityActivity;
import com.eibrahim.winkel.secondPages.SoundActivity;
import com.eibrahim.winkel.secondPages.SupportUsActivity;
import com.eibrahim.winkel.secondPages.ThemeActivity;
import com.eibrahim.winkel.secondPages.addPaymentMethodActivity;
import com.eibrahim.winkel.sign.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    LinearLayout btnProfile, btnPaymentMethods, btnOrders, btnLogout,
            btnAddNewItem, btnMyItems, btnMyOrders, btnSupport,
            btnAllUsers, for_admin, for_vendors,
            btnDarkMode, btnLanguages, btnSecurity,
            btnNotifications, btnSound, btnEvents,
            btnHelpFeedback, btnPermissions, btnAbout, btnDeleteAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_profile, container, false);

        btnProfile = rootView.findViewById(R.id.btnProfile);
        btnPaymentMethods = rootView.findViewById(R.id.btnPaymentMethods);
        btnMyOrders = rootView.findViewById(R.id.btnMyOrders);
        for_admin = rootView.findViewById(R.id.for_admin);
        btnOrders = rootView.findViewById(R.id.btnOrders);
        btnAllUsers = rootView.findViewById(R.id.btnAllUsers);
        for_vendors = rootView.findViewById(R.id.for_vendors);
        btnAddNewItem = rootView.findViewById(R.id.btnAddNewItem);
        btnMyItems = rootView.findViewById(R.id.btnMyItems);
        btnSupport = rootView.findViewById(R.id.btnSupport);
        btnHelpFeedback = rootView.findViewById(R.id.btnHelpFeedback);
        btnPermissions = rootView.findViewById(R.id.btnPermissions);
        btnAbout = rootView.findViewById(R.id.btnAbout);
        btnLogout = rootView.findViewById(R.id.btnLogout);
        btnDeleteAccount = rootView.findViewById(R.id.btnDeleteAccount);
        btnEvents = rootView.findViewById(R.id.btnEvents);
        btnSound = rootView.findViewById(R.id.btnSound);
        btnNotifications = rootView.findViewById(R.id.btnNotifications);
        btnSecurity = rootView.findViewById(R.id.btnSecurity);
        btnLanguages = rootView.findViewById(R.id.btnLanguages);
        btnDarkMode = rootView.findViewById(R.id.btnDarkMode);
        SwipeRefreshLayout profileFragment_layout = rootView.findViewById(R.id.profileFragment_layout);

        FetchUserType fetchUserType = new FetchUserType(for_admin, for_vendors);
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

            Intent intent = new Intent(getActivity(), SupportUsActivity.class);
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
        btnHelpFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HelpFeedbackActivity.class);
            startActivity(intent);
        });
        btnPermissions.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PermissionsActivity.class);
            startActivity(intent);
        });
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });
        btnDeleteAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DeleteAccountActivity.class);
            startActivity(intent);
        });
        btnSecurity.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SecurityActivity.class);
            startActivity(intent);
        });
        btnLanguages.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LanguagesActivity.class);
            startActivity(intent);
        });
        btnDarkMode.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ThemeActivity.class);
            startActivity(intent);
        });
        btnEvents.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventsActivity.class);
            startActivity(intent);
        });
        btnSound.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SoundActivity.class);
            startActivity(intent);
        });
        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationsActivity.class);
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