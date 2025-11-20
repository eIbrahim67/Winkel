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
    private String dataOfOrder = "";

    private long lastRefreshTime = 0;
    private final long refreshDelayMillis = 5000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        dataOfOrder = "";

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
                    dataOfOrder += itemId + "," + itemType + "," + much + "," + priceStr + "," + size + " & ";
                }

                return null;
            });

            tasks.add(task);
        }

        // When ALL product fetches complete
        Tasks.whenAllComplete(tasks).addOnSuccessListener(done -> {
            updateUI(result);

        }).addOnFailureListener(e -> showError(getString(R.string.error_loading)));
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
                new adapterRecyclerviewBasket(requireContext(), itemsList, this);

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
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), getText(R.string.page_refreshed_message), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), R.string.basket_empty_message, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(requireContext(), PaymentActivity.class);
            intent.putExtra("Total price", getFormattedPrice());
            intent.putExtra("Data", dataOfOrder);
            startActivity(intent);
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
