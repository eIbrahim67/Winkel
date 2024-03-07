package com.eibrahim.winkel.mianActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.secondActivity.PaymentActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewBasket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutFragment extends Fragment {

    public CheckoutFragment() {}

    private LinearLayout msgEmptyBasket;
    private TextView TotalPriceOfItems, noOfItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checkout, container, false);

        Button btn_checkout = rootView.findViewById(R.id.btn_checkout);
        SwipeRefreshLayout checkout_fragment = rootView.findViewById(R.id.checkout_fragment);
        noOfItems = rootView.findViewById(R.id.noOfItems);
        TotalPriceOfItems = rootView.findViewById(R.id.TotalPriceOfItemsCheckout);
        RecyclerView recyclerView_basket = rootView.findViewById(R.id.rv3);
        msgEmptyBasket = rootView.findViewById(R.id.msgEmptyBasket);

        fetchBasketData(recyclerView_basket, requireContext());

        checkout_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fetchBasketData(recyclerView_basket, requireContext());

                checkout_fragment.setRefreshing(false);

            }
        });

        btn_checkout.setOnClickListener(v -> {
            if (items > 0){
                Intent intent = new Intent(requireContext(), PaymentActivity.class);
                intent.putExtra("Total price", getTotalPriceBasket());
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Your BASKET is empty!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void fetchBasketData(RecyclerView recyclerView, Context context) {

        items = 0;
        much = 0.0;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = String.valueOf(auth.getCurrentUser().getUid());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<DataRecyclerviewItem> dataOfRvItems = new ArrayList<>();

        CollectionReference collectionRef = firestore.collection("UsersData")
                .document(userId).collection("BasketCollection");
        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        Map<String, Object> data = document.getData();

                        // TODO : assert data != null;
                        if (data != null){
                            DataRecyclerviewItem dataObject = new DataRecyclerviewItem(
                                    (String) data.get("category"),
                                    (String) data.get("imageId"),
                                    (String) data.get("name"),
                                    (String) data.get("price")
                            );

                            dataObject.setItemId((String) data.get("itemId"));
                            dataObject.setMuch((String) data.get("much"));
                            dataObject.setTotalPriceItem(String.valueOf(Double.parseDouble(dataObject.getmuch()) * Double.parseDouble(dataObject.getPrice())));
                            dataOfRvItems.add(dataObject);

                            addTotalPriceBasket(dataOfRvItems.get(items).getTotalPriceItem());
                            items++;

                        }

                    }
                    adapterRecyclerviewBasket adapterRvItems = new adapterRecyclerviewBasket(context, dataOfRvItems, CheckoutFragment.this);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    recyclerView.setAdapter(adapterRvItems);

                    if (items > 1)
                        noOfItems.setText(items + " items");
                    else
                        noOfItems.setText(items + " item");

                    TotalPriceOfItems.setText("$" +getTotalPriceBasket());

                    if (items == 0)
                        msgEmptyBasket.setVisibility(View.VISIBLE);
                    else
                        msgEmptyBasket.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                });

    }

    private double much = 0.0;

    public String getTotalPriceBasket() {
        return String.format("%.2f", much);
    }

    private int items = 0;

    private void addTotalPriceBasket(String s) {
        much += Double.parseDouble(s);
    }

    public void re(Double subs) {
        requireActivity().runOnUiThread(() -> {
            items--;
            much -= subs;
            TotalPriceOfItems.setText("$" + String.format("%.2f", much));
            if (items > 1)
                noOfItems.setText(String.valueOf(items + " items"));
            else
                noOfItems.setText(String.valueOf(items + " item"));
            if (items == 0)
                msgEmptyBasket.setVisibility(View.VISIBLE);
        });
    }
}