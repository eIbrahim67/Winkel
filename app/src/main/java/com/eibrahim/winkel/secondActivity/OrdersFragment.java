package com.eibrahim.winkel.secondActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.AdapterRecyclerviewOrders;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItemOrder;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItemOrderItemData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrdersFragment extends Fragment {

    Double totalPriceItem = 0.0;
    Double totalPriceOrder = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        RecyclerView recyclerView_orders = root.findViewById(R.id.re_orders);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);

        recyclerView_orders.setLayoutManager(gridLayoutManager);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<DataRecyclerviewItemOrder> dataOfRvItems = new ArrayList<>();
        List<DataRecyclerviewItemOrderItemData> dataOfRvItemData = new ArrayList<>();

        DocumentReference basketRef = firestore.collection("Orders")
                .document(userId);


        basketRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> orderslist = (List<String>) documentSnapshot.get("OrderCollection");
                if (orderslist != null) {
                    for (String itemIdType : orderslist) {

                        itemIdType =  itemIdType.substring(0, itemIdType.length() - 4);
                        String[] parts2;
                        String[] parts = itemIdType.split("&");

                        for (String item : parts) {

                            parts2 = item.split(",");

                            totalPriceItem = Double.parseDouble(parts2[3]) * Double.parseDouble(parts2[2]);

                            totalPriceOrder += totalPriceItem;

                            DataRecyclerviewItemOrderItemData itemData = new DataRecyclerviewItemOrderItemData(

                                    parts2[0],
                                    parts2[3],
                                    parts2[2],
                                    (String.valueOf(totalPriceItem)),
                                    parts2[4],
                                    parts2[1]

                            );

                            dataOfRvItemData.add(itemData);

                        }

                        DataRecyclerviewItemOrder order = new DataRecyclerviewItemOrder(

                                documentSnapshot.getId(),
                                dataOfRvItemData,
                                String.valueOf(totalPriceOrder)


                        );
                        dataOfRvItems.add(order);

                    }

                    AdapterRecyclerviewOrders adapterRecyclerviewOrders = new AdapterRecyclerviewOrders(
                            requireContext(),
                            dataOfRvItems
                    );

                    recyclerView_orders.setAdapter(adapterRecyclerviewOrders);
                }
            }
        });



        return root;

    }
}