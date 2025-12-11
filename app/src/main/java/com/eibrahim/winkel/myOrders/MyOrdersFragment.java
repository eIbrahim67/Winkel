package com.eibrahim.winkel.myOrders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.AdapterRecyclerviewOrders;
import com.eibrahim.winkel.core.DataOrderItem;
import com.eibrahim.winkel.core.DataRecyclerviewItemOrderItemData;
import com.eibrahim.winkel.databinding.ActivityMyOrdersBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyOrdersFragment extends Fragment {

    private ActivityMyOrdersBinding binding;

    private FirebaseFirestore firestore;
    private String userId;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityMyOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.reMyOrders.setLayoutManager(new GridLayoutManager(requireContext(), 1));

        firestore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());

        fetchOrders();
    }

    private void fetchOrders() {
        DocumentReference orderRef = firestore.collection("Orders").document(userId);

        orderRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> ordersList = (List<String>) documentSnapshot.get("OrderCollection");

                if (ordersList == null || ordersList.isEmpty()) {
                    Toast.makeText(requireContext(), getText(R.string.there_are_currently_no_orders_to_display), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<DataOrderItem> orderItems = new ArrayList<>();

                for (String orderData : ordersList) {
                    orderItems.add(parseOrder(orderData));
                }

                AdapterRecyclerviewOrders adapter = new AdapterRecyclerviewOrders(requireContext(), orderItems);
                binding.reMyOrders.setAdapter(adapter);
            } else {
                Toast.makeText(requireContext(), getText(R.string.there_are_currently_no_orders_to_display), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(requireContext(), R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show());
    }

    private DataOrderItem parseOrder(String orderData) {
        List<DataRecyclerviewItemOrderItemData> items = new ArrayList<>();
        double totalOrderPrice = 0.0;

        try {
            orderData = orderData.trim();
            if (orderData.endsWith("&")) {
                orderData = orderData.substring(0, orderData.length() - 1).trim();
            }

            String[] itemStrings = orderData.split("&");
            for (String item : itemStrings) {
                String[] parts = item.split(",");

                if (parts.length < 5) continue;

                String itemId = parts[0].trim();
                String itemType = parts[1].trim();
                double quantity = Double.parseDouble(parts[2].trim());
                double price = Double.parseDouble(parts[3].trim());
                String size = parts[4].trim();

                double itemTotal = quantity * price;
                totalOrderPrice += itemTotal;

                items.add(new DataRecyclerviewItemOrderItemData(itemId, String.valueOf(price), String.valueOf(quantity), String.valueOf(itemTotal), size, itemType));
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
        }

        return new DataOrderItem(userId, items, String.format("%.2f", totalOrderPrice));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
