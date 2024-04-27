package com.eibrahim.winkel.secondPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.AdapterRecyclerviewOrders;
import com.eibrahim.winkel.dataClasses.DataOrderItem;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItemOrderItemData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    Double totalPriceItem = 0.0;
    Double totalPriceOrder = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        RecyclerView recyclerView_orders = findViewById(R.id.re_orders);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);

        recyclerView_orders.setLayoutManager(gridLayoutManager);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<DataOrderItem> dataOfRvItems = new ArrayList<>();
        List<DataRecyclerviewItemOrderItemData> dataOfRvItemData = new ArrayList<>();

        CollectionReference ordersRef = firestore.collection("Orders");

        ordersRef.get().addOnSuccessListener(documentSnapshot -> {

            for ( QueryDocumentSnapshot data :documentSnapshot) {

                if (data.exists()) {
                    List<String> orderslist = (List<String>) data.get("OrderCollection");

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
                                    Toast.makeText(OrdersActivity.this, getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
                                }
                            }

                            DataOrderItem order = new DataOrderItem(

                                    data.getId(),
                                    dataOfRvItemData,
                                    String.valueOf(totalPriceOrder)


                            );
                            dataOfRvItems.add(order);

                        }

                        AdapterRecyclerviewOrders adapterRecyclerviewOrders = new AdapterRecyclerviewOrders(
                                OrdersActivity.this,
                                dataOfRvItems
                        );

                        recyclerView_orders.setAdapter(adapterRecyclerviewOrders);
                    }
                }

            }

        });

    }
}