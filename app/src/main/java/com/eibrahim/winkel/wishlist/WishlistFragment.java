package com.eibrahim.winkel.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.eibrahim.winkel.databinding.FragmentWishlistBinding;
import com.eibrahim.winkel.core.FetchDataFromFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class WishlistFragment extends Fragment {

    private FragmentWishlistBinding binding;
    private final String userId = FirebaseAuth.getInstance().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);

        try {
            FetchDataFromFirebase fetchData = new FetchDataFromFirebase(binding.recyclerviewWishlist, requireContext());

            fetchData.fetchSpecific("UsersData", userId, "Wishlist", binding.msgEmptyWishlist);

            binding.wishlistFragment.setOnRefreshListener(() -> {
                try {
                    binding.msgEmptyWishlist.setVisibility(View.INVISIBLE);
                    fetchData.fetchSpecific("UsersData", userId, "Wishlist", binding.msgEmptyWishlist);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    binding.wishlistFragment.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
