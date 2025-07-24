package com.eibrahim.winkel.vendorPages;

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

import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.eibrahim.winkel.databinding.ActivityMyItemsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MyItemsFragment extends Fragment {

    private ActivityMyItemsBinding binding;
    private final List<DataRecyclerviewMyItem> userItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityMyItemsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        List<String> types = Arrays.asList("Mens", "Womens", "Kids");

        fetchAllMyUploadedItems(types);

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void fetchAllMyUploadedItems(List<String> types) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (String type : types) {
            FirebaseFirestore.getInstance()
                    .collection("Products")
                    .document(type)
                    .collection(type)
                    .whereEqualTo("userId", currentUserId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Map<String, Object> data = document.getData();
                            if (data != null) {
                                DataRecyclerviewMyItem item = new DataRecyclerviewMyItem(
                                        (String) data.get("category"),
                                        (String) data.get("imageId"),
                                        (String) data.get("name"),
                                        (String) data.get("price"),
                                        type,
                                        ""
                                );
                                item.setItemId((String) data.get("itemId"));
                                userItems.add(item);
                            }
                        }

                        adapterRecyclerviewMyItems adapter =
                                new adapterRecyclerviewMyItems(requireContext(), userItems);
                        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                        binding.recyclerView.setAdapter(adapter);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("FetchMyUploads", "Failed to fetch from " + type, e);
                        Toast.makeText(requireContext(), "Error loading your uploads", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
