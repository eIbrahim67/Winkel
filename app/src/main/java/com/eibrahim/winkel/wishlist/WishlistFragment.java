package com.eibrahim.winkel.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.core.FetchDataFromFirebase;
import com.eibrahim.winkel.databinding.FragmentWishlistBinding;
import com.google.firebase.auth.FirebaseAuth;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            binding.msgEmptyWishlist.setVisibility(View.VISIBLE);
            return;
        }

        FetchDataFromFirebase fetchData = new FetchDataFromFirebase(
                binding.recyclerviewWishlist,
                binding.skeletonLayout
        );

        loadWishlist(fetchData);

        // Pull-to-refresh
        binding.wishlistFragment.setOnRefreshListener(() -> {
            binding.msgEmptyWishlist.setVisibility(View.INVISIBLE);
            loadWishlist(fetchData);
            binding.wishlistFragment.setRefreshing(false);
        });
    }

    private void loadWishlist(FetchDataFromFirebase fetchData) {
        fetchData.fetchSpecific(
                "UsersData",
                userId,
                "Wishlist",
                binding.msgEmptyWishlist
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
