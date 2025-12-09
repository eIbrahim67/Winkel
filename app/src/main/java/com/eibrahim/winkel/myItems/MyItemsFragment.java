package com.eibrahim.winkel.myItems;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.databinding.ActivityMyItemsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MyItemsFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private ActivityMyItemsBinding binding;

    private final List<DataRecyclerviewMyItem> userItems = new ArrayList<>();
    private int completedFetches = 0;

    private final List<String> types = Arrays.asList("Mens", "Womens", "Kids");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityMyItemsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        loadUserItems();
    }

    // -------------------------------------------------------------------------
    // LOAD USER UPLOADED ITEMS
    // -------------------------------------------------------------------------
    private void loadUserItems() {
        showLoading();

        userItems.clear();
        completedFetches = 0;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String type : types) {

            db.collection("Products")
                    .document(type)
                    .collection(type)
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener(query -> {

                        if (!isAdded() || binding == null) return;

                        for (DocumentSnapshot snapshot : query.getDocuments()) {
                            Map<String, Object> data = snapshot.getData();
                            if (data == null) continue;

                            DataRecyclerviewMyItem item = new DataRecyclerviewMyItem(
                                    safeString(data.get("category")),
                                    safeString(data.get("imageId")),
                                    safeString(data.get("name")),
                                    safeString(data.get("price")),
                                    type,
                                    ""
                            );

                            item.setItemId(safeString(data.get("itemId")));
                            userItems.add(item);
                        }

                        handleFetchCompletion();

                    }).addOnFailureListener(e -> {
                        if (!isAdded() || binding == null) return;

                        Log.e("FetchMyUploads", "Error fetching from " + type, e);
                        Toast.makeText(requireContext(),
                                R.string.an_error_occurred_while_loading_your_uploads,
                                Toast.LENGTH_SHORT).show();

                        handleFetchCompletion();
                    });
        }
    }

    // Called after each category fetch completes
    private void handleFetchCompletion() {
        completedFetches++;

        if (completedFetches < types.size()) return;

        // All fetching finished
        if (!isAdded() || binding == null) return;

        hideLoading();

        if (userItems.isEmpty()) {
            binding.msgEmptyWishlist.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            return;
        }

        binding.msgEmptyWishlist.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);

        adapterRecyclerviewMyItems adapter =
                new adapterRecyclerviewMyItems(requireContext(), userItems);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerView.setAdapter(adapter);
    }

    // -------------------------------------------------------------------------
    // UI Helpers
    // -------------------------------------------------------------------------
    private void showLoading() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.msgEmptyWishlist.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.loadingIndicator.setVisibility(View.GONE);
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
