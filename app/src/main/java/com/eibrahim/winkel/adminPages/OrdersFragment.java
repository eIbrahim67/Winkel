package com.eibrahim.winkel.adminPages;

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
import com.eibrahim.winkel.adapterClasses.AdapterRecyclerviewOrders;
import com.eibrahim.winkel.dataClasses.DataOrderItem;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItemOrderItemData;
import com.eibrahim.winkel.databinding.ActivityOrdersBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private ActivityOrdersBinding binding;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = ActivityOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.reOrders.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        firestore = FirebaseFirestore.getInstance();

        fetchAllOrders();
    }

    private void fetchAllOrders() {
        CollectionReference ordersRef = firestore.collection("Orders");

        ordersRef.get().addOnSuccessListener(querySnapshot -> {
            List<DataOrderItem> allOrders = new ArrayList<>();

            for (QueryDocumentSnapshot userDoc : querySnapshot) {
                String userId = userDoc.getId();
                List<String> userOrders = (List<String>) userDoc.get("OrderCollection");

                if (userOrders == null || userOrders.isEmpty()) continue;

                for (String orderString : userOrders) {
                    DataOrderItem orderItem = parseOrder(userId, orderString);
                    if (orderItem != null) {
                        allOrders.add(orderItem);
                    }
                }
            }

            if (allOrders.isEmpty()) {
                Toast.makeText(requireContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
            }

            AdapterRecyclerviewOrders adapter = new AdapterRecyclerviewOrders(requireContext(), allOrders);
            binding.reOrders.setAdapter(adapter);
        }).addOnFailureListener(e ->
                Toast.makeText(requireContext(), R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show()
        );
    }

    private DataOrderItem parseOrder(String userId, String orderString) {
        List<DataRecyclerviewItemOrderItemData> items = new ArrayList<>();
        double totalOrderPrice = 0.0;

        try {
            orderString = orderString.trim();
            if (orderString.endsWith("&")) {
                orderString = orderString.substring(0, orderString.length() - 1).trim();
            }

            String[] itemStrings = orderString.split("&");

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

                items.add(new DataRecyclerviewItemOrderItemData(
                        itemId, String.valueOf(price), String.valueOf(quantity),
                        String.valueOf(itemTotal), size, itemType
                ));
            }

            return new DataOrderItem(userId, items, String.format("%.2f", totalOrderPrice));

        } catch (Exception e) {
            Toast.makeText(requireContext(), getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
