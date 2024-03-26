package com.eibrahim.winkel.secondPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

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

public class MyOrdersActivity extends AppCompatActivity {

    Double totalPriceItem = 0.0;
    Double totalPriceOrder = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        RecyclerView recyclerView_orders = findViewById(R.id.re_my_orders);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);

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

                        String[] parts = itemIdType.split("&");

                        for (String item : parts) {


                            String[] parts2 = item.split(",");

                            totalPriceItem = Double.parseDouble(parts2[3]) * Double.parseDouble(parts2[2]);

                            totalPriceOrder += totalPriceItem;

                            try {
                                DataRecyclerviewItemOrderItemData itemData = new DataRecyclerviewItemOrderItemData(

                                        parts2[0],
                                        parts2[3],
                                        parts2[2],
                                        (String.valueOf(totalPriceItem)),
                                        parts2[4],
                                        parts2[1]

                                );

                                dataOfRvItemData.add(itemData);
                            }catch (Exception e) {
                                Toast.makeText(MyOrdersActivity.this, "Unexpected error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        DataRecyclerviewItemOrder order = new DataRecyclerviewItemOrder(

                                documentSnapshot.getId(),
                                dataOfRvItemData,
                                String.valueOf(totalPriceOrder)


                        );
                        dataOfRvItems.add(order);

                    }

                    AdapterRecyclerviewOrders adapterRecyclerviewOrders = new AdapterRecyclerviewOrders(
                            MyOrdersActivity.this,
                            dataOfRvItems
                    );

                    recyclerView_orders.setAdapter(adapterRecyclerviewOrders);
                }
            }
        });

    }
}