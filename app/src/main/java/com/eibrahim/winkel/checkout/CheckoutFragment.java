package com.eibrahim.winkel.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.databinding.FragmentCheckoutBinding;
import com.eibrahim.winkel.payment.PaymentActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckoutFragment extends Fragment {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final long refreshDelayMillis = 5000;
    private FragmentCheckoutBinding binding;
    private String userId;
    private double totalPrice = 0.0;
    private int items = 0;
    private long lastRefreshTime = 0;
    private adapterRecyclerviewBasket adapter = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        setupSwipeRefresh();
        setupCheckoutButton();

        loadBasket();

        return binding.getRoot();
    }

    // -----------------------------
    // FETCH BASKET + PRODUCTS FAST
    // -----------------------------

    private void loadBasket() {

        showLoading();

        firestore.collection("UsersData")
                .document(userId)
                .collection("Basket")
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        showEmptyBasket();
                        return;
                    }

                    List<DataRecyclerviewMyItem> result = new ArrayList<>();

                    items = 0;
                    totalPrice = 0.0;

                    for (DocumentSnapshot doc : query) {

                        String itemId = doc.getString("itemId");
                        String itemType = doc.getString("itemType");
                        String size = doc.getString("size");

                        long quantity = doc.getLong("quantity") != null
                                ? doc.getLong("quantity") : 1;

                        double price = doc.getDouble("price") != null
                                ? doc.getDouble("price") : 0;

                        double itemTotal = price * quantity;

                        // fetch product info
                        fetchProduct(itemId, itemType, size,
                                quantity, price, itemTotal, result);
                    }

                })
                .addOnFailureListener(e ->
                        showError(getString(R.string.error_loading)));
    }


    private void fetchProduct(
            String itemId,
            String itemType,
            String size,
            long quantity,
            double price,
            double itemTotal,
            List<DataRecyclerviewMyItem> result) {

        firestore.collection("Products")
                .document(itemType)
                .collection(itemType)
                .document(itemId)
                .get()
                .addOnSuccessListener(snap -> {

                    if (!snap.exists()) return;

                    String name = snap.getString("name");
                    String category = snap.getString("category");
                    String imageId = snap.getString("imageId");

                    DataRecyclerviewMyItem item =
                            new DataRecyclerviewMyItem(
                                    category,
                                    imageId,
                                    name,
                                    String.valueOf(price),
                                    itemType,
                                    size
                            );

                    item.setItemId(itemId);
                    item.setMuch(String.valueOf(quantity));
                    item.setTotalPriceItem(itemTotal);

                    result.add(item);

                    items++;
                    totalPrice += itemTotal;

                    updateUI(result);
                });
    }

    // -----------------------------
    // UI UPDATE
    // -----------------------------
    private void updateUI(List<DataRecyclerviewMyItem> itemsList) {
        hideLoading();

        if (itemsList.isEmpty()) {
            showEmptyBasket();
            return;
        }

        adapter = new adapterRecyclerviewBasket(itemsList, this);

        binding.rv3.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        binding.rv3.setAdapter(adapter);

        binding.noOfItems.setText(items + (items == 1 ? getString(R.string.item) : getString(R.string.items)));
        binding.totalPriceOfItemsCheckout.setText(getFormattedPrice());
    }

    // -----------------------------
    // UI HELPERS
    // -----------------------------
    private void showLoading() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.msgEmptyBasket.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.loadingIndicator.setVisibility(View.GONE);
    }

    private void showEmptyBasket() {
        hideLoading();
        binding.msgEmptyBasket.setVisibility(View.VISIBLE);
        binding.totalPriceOfItemsCheckout.setText("0");
    }

    private void showError(String msg) {
        hideLoading();
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    // -----------------------------
    // SWIPE REFRESH
    // -----------------------------
    private void setupSwipeRefresh() {
        binding.checkoutFragment.setOnRefreshListener(() -> {
            binding.noOfItems.setText(R.string._0_items);
            totalPrice = 0;
            items = 0;
            if (adapter != null)
                adapter.clear();
            long now = System.currentTimeMillis();

            if (now - lastRefreshTime >= refreshDelayMillis) {
                loadBasket();
                lastRefreshTime = now;
            } else {
                Snackbar.make(requireView(), getText(R.string.page_refreshed_message), Snackbar.LENGTH_SHORT).show();
            }

            binding.checkoutFragment.setRefreshing(false);
        });
    }

    // -----------------------------
    // CHECKOUT BUTTON
    // -----------------------------
    private void setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener(v -> {
            if (items == 0) {
                Snackbar.make(requireView(), R.string.basket_empty_message, Snackbar.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), PaymentActivity.class));
        });
    }

    // -----------------------------
    // HELPERS
    // -----------------------------
    public String getFormattedPrice() {
        return String.format("%.2f", totalPrice);
    }

    // Called when items change (remove or update)
    public void updateAfterChange(double amount, char type) {
        if (type == '+')
            totalPrice += amount;
        else
            totalPrice -= amount;

        updateSmallUI();
    }

    public void removeItem(double amount) {
        items--;
        totalPrice -= amount;
        updateSmallUI();
    }

    private void updateSmallUI() {
        binding.noOfItems.setText(items + (items == 1 ? getString(R.string.item) : getString(R.string.items)));
        binding.totalPriceOfItemsCheckout.setText(getFormattedPrice());

        if (items == 0)
            showEmptyBasket();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
