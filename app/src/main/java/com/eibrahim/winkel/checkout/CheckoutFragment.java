package com.eibrahim.winkel.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.databinding.FragmentCheckoutBinding;
import com.eibrahim.winkel.payment.PaymentActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String userId;

    private double totalPrice = 0.0;
    private int items = 0;

    private long lastRefreshTime = 0;
    private final long refreshDelayMillis = 5000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setupSwipeRefresh();
        setupCheckoutButton();

        loadBasket();

        return binding.getRoot();
    }

    // -------------------------------------------------------------------------
    // LOAD BASKET
    // -------------------------------------------------------------------------
    private void loadBasket() {
        if (binding == null) return;

        showLoading();

        DocumentReference basketRef = firestore.collection("UsersData").document(userId).collection("BasketCollection").document("BasketDocument");

        basketRef.get().addOnSuccessListener(snap -> {

            if (!isAdded() || binding == null) return;

            List<String> basket = (List<String>) snap.get("BasketCollection");

            if (basket == null || basket.isEmpty()) {
                showEmptyBasket();
                return;
            }

            fetchAllProducts(basket);

        }).addOnFailureListener(e -> {
            if (isAdded()) showError(getString(R.string.error_loading));
        });
    }

    // -------------------------------------------------------------------------
    // FETCH PRODUCTS IN PARALLEL
    // -------------------------------------------------------------------------
    private void fetchAllProducts(List<String> basket) {

        List<Task<?>> tasks = new ArrayList<>();
        List<DataRecyclerviewMyItem> result = new ArrayList<>();

        totalPrice = 0.0;
        items = 0;

        for (String entry : basket) {
            String[] parts = entry.split(",");

            if (parts.length < 4) continue;

            String itemId = parts[0].trim();
            String itemType = parts[1].trim();
            String qtyStr = parts[2].trim();
            String size = parts[3].trim();

            DocumentReference productRef = firestore.collection("Products").document(itemType).collection(itemType).document(itemId);

            Task<?> task = productRef.get().continueWith(productTask -> {

                if (!productTask.isSuccessful() || !productTask.getResult().exists()) return null;

                Map<String, Object> data = productTask.getResult().getData();
                if (data == null) return null;

                String name = safeString(data.get("name"));
                String category = safeString(data.get("category"));
                String imageId = safeString(data.get("imageId"));

                double price = safeDouble(data.get("price"));
                double qty = safeDouble(qtyStr);

                double totalForItem = price * qty;

                DataRecyclerviewMyItem item = new DataRecyclerviewMyItem(category, imageId, name, String.valueOf(price), itemType, size);

                item.setItemId(itemId);
                item.setMuch(qtyStr);
                item.setTotalPriceItem(totalForItem);

                synchronized (result) {
                    result.add(item);
                    totalPrice += totalForItem;
                    items++;
                }

                return null;
            });

            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks).addOnSuccessListener(done -> {
            if (!isAdded() || binding == null) return;
            updateUI(result);

        }).addOnFailureListener(e -> {
            if (isAdded()) showError(getString(R.string.error_loading));
        });
    }

    // -------------------------------------------------------------------------
    // UPDATE UI
    // -------------------------------------------------------------------------
    private void updateUI(List<DataRecyclerviewMyItem> itemsList) {

        hideLoading();
        if (binding == null) return;

        if (itemsList.isEmpty()) {
            showEmptyBasket();
            return;
        }

        adapterRecyclerviewBasket adapter = new adapterRecyclerviewBasket(requireContext(), itemsList, this);

        binding.rv3.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rv3.setAdapter(adapter);

        updateSmallUI();
    }

    // -------------------------------------------------------------------------
    // UI HELPERS
    // -------------------------------------------------------------------------
    private void updateSmallUI() {
        if (binding == null) return;

        binding.noOfItems.setText(items + (items == 1 ? getString(R.string.item) : getString(R.string.items)));

        binding.totalPriceOfItemsCheckout.setText(getFormattedPrice());

        if (items == 0) showEmptyBasket();
    }

    private void showLoading() {
        if (binding == null) return;
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.msgEmptyBasket.setVisibility(View.GONE);
        binding.rv3.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (binding == null) return;
        binding.loadingIndicator.setVisibility(View.GONE);
    }

    private void showEmptyBasket() {
        if (binding == null) return;
        hideLoading();
        binding.msgEmptyBasket.setVisibility(View.VISIBLE);
        binding.rv3.setVisibility(View.GONE);
        binding.totalPriceOfItemsCheckout.setText("0");
        items = 0;
    }

    private void showError(String msg) {
        hideLoading();
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    // -------------------------------------------------------------------------
    // SWIPE REFRESH
    // -------------------------------------------------------------------------
    private void setupSwipeRefresh() {
        binding.checkoutFragment.setOnRefreshListener(() -> {

            long now = System.currentTimeMillis();

            if (now - lastRefreshTime >= refreshDelayMillis) {
                loadBasket();
                lastRefreshTime = now;
            } else {
                Toast.makeText(requireContext(), getText(R.string.page_refreshed_message), Toast.LENGTH_SHORT).show();
            }

            binding.checkoutFragment.setRefreshing(false);
        });
    }

    // -------------------------------------------------------------------------
    // CHECKOUT BUTTON
    // -------------------------------------------------------------------------
    private void setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener(v -> {
            if (items == 0) {
                Toast.makeText(requireContext(), R.string.basket_empty_message, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(requireContext(), PaymentActivity.class));
        });
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------
    public String getFormattedPrice() {
        return String.format("%.2f", totalPrice);
    }

    public void updateAfterChange(double amount, char type) {
        if (type == '+') totalPrice += amount;
        else totalPrice -= amount;
        updateSmallUI();
    }

    public void removeItem(double amount) {
        items--;
        totalPrice -= amount;
        updateSmallUI();
    }

    private String safeString(Object value) {
        return value == null ? "" : value.toString();
    }

    private double safeDouble(Object value) {
        if (value == null) return 0.0;

        try {
            if (value instanceof Number) return ((Number) value).doubleValue();
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
