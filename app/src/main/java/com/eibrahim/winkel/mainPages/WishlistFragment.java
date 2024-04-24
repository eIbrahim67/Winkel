package com.eibrahim.winkel.mainPages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.FetchDataFromFirebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private final List<String> wishlistIds = new ArrayList<>();
    private final String userId = FirebaseAuth.getInstance().getUid();
    private LinearLayout msgEmptyWishlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        RecyclerView recyclerview_wishlist = rootView.findViewById(R.id.recyclerview_wishlist);

        SwipeRefreshLayout wishlist_fragment = rootView.findViewById(R.id.wishlist_fragment);

        msgEmptyWishlist = rootView.findViewById(R.id.msgEmptyWishlist);

        FetchDataFromFirebase fetchData = new FetchDataFromFirebase(recyclerview_wishlist, requireContext());

        fetchData.fetchSpecific("UsersData", userId, "Wishlist");


        wishlist_fragment.setOnRefreshListener(() -> {

            fetchData.fetchSpecific("UsersData", userId, "Wishlist");

            wishlist_fragment.setRefreshing(false);

        });

        return rootView;
    }


}