package com.eibrahim.winkel.checkout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.databinding.FragmentCheckoutBinding;
import com.eibrahim.winkel.payment.PaymentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String dataOfOrder = "";
    private double totalPrice = 0.0;
    private int items = 0;
    private long lastRefreshTime = 0;
    private final long refreshDelayMillis = 5000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        try {
            fetchBasketData(requireContext());

            binding.checkoutFragment.setOnRefreshListener(() -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastRefreshTime >= refreshDelayMillis) {
                    fetchBasketData(requireContext());
                    binding.checkoutFragment.setRefreshing(false);
                    lastRefreshTime = currentTime;
                } else {
                    Toast.makeText(requireContext(), getText(R.string.page_refreshed_message), Toast.LENGTH_SHORT).show();
                    binding.checkoutFragment.setRefreshing(false);
                }
            });

            binding.btnCheckout.setOnClickListener(v -> {
                if (items > 0) {
                    Intent intent = new Intent(requireContext(), PaymentActivity.class);
                    intent.putExtra("Total price", getFormattedTotalPrice());
                    intent.putExtra("Data", dataOfOrder);
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), getText(R.string.basket_empty_message), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }

    private void fetchBasketData(Context context) {
        try {
            items = 0;
            totalPrice = 0.0;
            dataOfOrder = "";
            binding.msgEmptyBasket.setVisibility(View.GONE);

            List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();
            DocumentReference basketRef = firestore.collection("UsersData").document(userId).collection("BasketCollection").document("BasketDocument");

            basketRef.get().addOnSuccessListener(documentSnapshot -> {
                try {
                    if (documentSnapshot.exists()) {
                        List<String> wishlist = (List<String>) documentSnapshot.get("BasketCollection");
                        if (wishlist == null || wishlist.isEmpty()) {
                            binding.msgEmptyBasket.setVisibility(View.VISIBLE);
                            updateBasketUI(dataOfRvItems, context);
                            return;
                        }

                        for (String itemIdType : wishlist) {
                            try {
                                String[] parts = itemIdType.split(",");
                                String itemId = parts[0].trim();
                                String itemType = parts[1].trim();
                                String itemMuch = parts[2].trim();
                                String itemSize = parts[3].trim();

                                DocumentReference productRef = firestore.collection("Products").document(itemType).collection(itemType).document(itemId);

                                productRef.get().addOnSuccessListener(productSnap -> {
                                    try {
                                        if (productSnap.exists() && productSnap.getData() != null) {
                                            Map<String, Object> data = productSnap.getData();
                                            String category = (String) data.get("category");
                                            String imageId = (String) data.get("imageId");
                                            String name = (String) data.get("name");
                                            String price = (String) data.get("price");
                                            String itemPrice = price == null ? "0" : price;

                                            DataRecyclerviewMyItem dataItem = new DataRecyclerviewMyItem(category, imageId, name, itemPrice, itemType, itemSize);
                                            dataItem.setItemId((String) data.get("itemId"));
                                            dataItem.setMuch(itemMuch);

                                            double totalItemPrice = Double.parseDouble(itemMuch) * Double.parseDouble(itemPrice);
                                            dataItem.setTotalPriceItem(totalItemPrice);
                                            dataOfRvItems.add(dataItem);

                                            totalPrice += totalItemPrice;
                                            items++;
                                            dataOfOrder += itemId + "," + itemType + "," + itemMuch + "," + itemPrice + "," + itemSize + " & ";

                                            updateBasketUI(dataOfRvItems, context);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        binding.msgEmptyBasket.setVisibility(View.VISIBLE);
                        updateBasketUI(dataOfRvItems, context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBasketUI(List<DataRecyclerviewMyItem> dataList, Context context) {
        requireActivity().runOnUiThread(() -> {
            adapterRecyclerviewBasket adapter = new adapterRecyclerviewBasket(context, dataList, CheckoutFragment.this);
            binding.rv3.setLayoutManager(new GridLayoutManager(context, 1));
            binding.rv3.setAdapter(adapter);

            String itemText = items + (items == 1 ? getString(R.string.item) : getString(R.string.items));
            binding.noOfItems.setText(itemText);
            binding.totalPriceOfItemsCheckout.setText(getFormattedTotalPrice());

            if (items == 0) {
                binding.msgEmptyBasket.setVisibility(View.VISIBLE);
                binding.totalPriceOfItemsCheckout.setText("0");
            }
        });
    }

    public String getFormattedTotalPrice() {
        return String.format("%.2f", totalPrice);
    }

    public void re(Double amount) {
        requireActivity().runOnUiThread(() -> {
            items--;
            totalPrice -= amount;
            updateBasketUIAfterChange();
        });
    }

    public void re(Double amount, Character type) {
        requireActivity().runOnUiThread(() -> {
            totalPrice = type.equals('+') ? totalPrice + amount : totalPrice - amount;
            updateBasketUIAfterChange();
        });
    }

    private void updateBasketUIAfterChange() {
        String itemText = items + (items == 1 ? getString(R.string.item) : getString(R.string.items));
        binding.noOfItems.setText(itemText);
        binding.totalPriceOfItemsCheckout.setText(getFormattedTotalPrice());

        if (items == 0) {
            binding.msgEmptyBasket.setVisibility(View.VISIBLE);
            binding.totalPriceOfItemsCheckout.setText("0");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
