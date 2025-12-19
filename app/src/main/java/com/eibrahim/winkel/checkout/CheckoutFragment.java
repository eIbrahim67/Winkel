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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CheckoutFragment extends Fragment {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final long refreshDelayMillis = 5000;
    private FragmentCheckoutBinding binding;
    private String userId;
    private double totalPrice = 0.0;
    private int items = 0;
    private long lastRefreshTime = 0;

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

        DocumentReference basketRef = firestore.collection("UsersData")
                .document(userId)
                .collection("BasketCollection")
                .document("BasketDocument");

        basketRef.get().addOnSuccessListener(snap -> {
            List<String> basket = (List<String>) snap.get("BasketCollection");

            if (basket == null || basket.isEmpty()) {
                showEmptyBasket();
                return;
            }

            fetchAllProducts(basket);

        }).addOnFailureListener(e -> showError(getString(R.string.error_loading)));
    }

    // Fetch all product documents in parallel
    private void fetchAllProducts(List<String> basket) {
        List<Task<?>> tasks = new ArrayList<>();
        List<DataRecyclerviewMyItem> result = new ArrayList<>();

        // RESET totals
        items = 0;
        totalPrice = 0.0;

        for (String entry : basket) {
            String[] parts = entry.split(",");

            String itemId = parts[0].trim();
            String itemType = parts[1].trim();
            String much = parts[2].trim();
            String size = parts[3].trim();

            DocumentReference productRef = firestore.collection("Products")
                    .document(itemType)
                    .collection(itemType)
                    .document(itemId);

            Task<?> task = productRef.get().continueWith(productTask -> {
                if (!productTask.isSuccessful() || !productTask.getResult().exists())
                    return null;

                Map<String, Object> data = productTask.getResult().getData();
                if (data == null) return null;

                String name = (String) data.get("name");
                String category = (String) data.get("category");
                String imageId = (String) data.get("imageId");
                String priceStr = (String) data.get("price");
                double price = priceStr == null ? 0 : Double.parseDouble(priceStr);

                double total = price * Double.parseDouble(much);

                DataRecyclerviewMyItem item = new DataRecyclerviewMyItem(
                        category, imageId, name, priceStr, itemType, size
                );

                item.setItemId(itemId);
                item.setMuch(much);
                item.setTotalPriceItem(total);

                synchronized (result) {
                    result.add(item);
                    items++;
                    totalPrice += total;
                }

                return null;
            });

            tasks.add(task);
        }

        // When ALL product fetches complete
        Tasks.whenAllComplete(tasks).addOnSuccessListener(done -> updateUI(result)).addOnFailureListener(e -> showError(getString(R.string.error_loading)));
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

        adapterRecyclerviewBasket adapter =
                new adapterRecyclerviewBasket(itemsList, this);

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
