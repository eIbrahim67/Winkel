package com.eibrahim.winkel.payment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.databinding.ActivityPaymentBinding;
import com.eibrahim.winkel.main.LocaleHelper;
import com.eibrahim.winkel.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    private final Map<String, String> totalData = new HashMap<>();
    private ActivityPaymentBinding binding;
    private boolean isAllAddressAdded = false;
    private PaymentSheet paymentSheet;
    private String clientSecret;
    private long amountInPiasters;
    private double totalPrice = 0.0;
    private int items = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");
        Context context = LocaleHelper.setLocale(newBase, language);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Theme
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        int isDarkMode = sharedPreferences.getInt("theme_state", -1);
        if (isDarkMode != -1) {
            AppCompatDelegate.setDefaultNightMode(isDarkMode == 1 ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        // Initialize Stripe
        PaymentConfiguration.init(getApplicationContext(), "pk_test_51SVFRGFpTKioic1Y7QUBbLvSCsKYzOa5eIkMaTMHy7XBZ32qHUK2SlferifioQ1LBELzqOdOO26aovGefEYwONpn00WTwiPQmQ");

        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Stripe PaymentSheet init
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);
        binding.deliveryCostPayment.setText(String.format("%.2f", 20.00) + getString(R.string.le));
        loadBasket();

        // Address TextWatchers
        TextWatcher addressWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAddressFields();
            }
        };
        binding.addressState.addTextChangedListener(addressWatcher);
        binding.addressCity.addTextChangedListener(addressWatcher);
        binding.addressStreet.addTextChangedListener(addressWatcher);

        binding.btnBack.setOnClickListener(v -> finish());

        // Payment button click
        binding.btnPayment.setOnClickListener(v -> {
            binding.btnPayment.setEnabled(false);
            binding.btnPayment.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
            if (!isAllAddressAdded) {
                binding.btnPayment.setEnabled(true);
                binding.btnPayment.setText(R.string.payment);
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), R.string.please_complete_all_address_details_before_proceeding, Snackbar.LENGTH_SHORT).show();
                return;
            }
            createPaymentIntent(amountInPiasters);
        });
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
//                        showEmptyBasket();
                        finish();
                        return;
                    }

                    List<DataRecyclerviewMyItem> result = new ArrayList<>();

                    items = 0;
                    totalPrice = 0.0;
                    totalData.clear();

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

    // -----------------------------
    // UI HELPERS
    // -----------------------------
    private void showLoading() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.loadingIndicator.setVisibility(View.GONE);
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

                    totalData.put(itemId,
                            itemId + "," + itemType + "," +
                                    quantity + "," + price + "," + size);

                    updateUI(result);
                });
    }


    // UI UPDATE
    // -----------------------------
    private void updateUI(List<DataRecyclerviewMyItem> itemsList) {
        adapterRecyclerviewBasketPayment adapter = new adapterRecyclerviewBasketPayment(itemsList, this);

        binding.rv3.setLayoutManager(new GridLayoutManager(this, 1));
        binding.rv3.setAdapter(adapter);

        binding.noOfItems.setText(items + (items == 1 ? getString(R.string.item) : getString(R.string.items)));

        double totalFinalPrice = Double.parseDouble(normalizeNumber(String.valueOf(getFormattedPrice()))) + 20.00;
        amountInPiasters = (long) (totalFinalPrice * 100); // Stripe amount in cents
        binding.subTotalPriceOfItemsPayment.setText(getFormattedPrice() + getString(R.string.le));
        binding.totalPriceOfItemsPayment.setText(String.format("%.2f", totalFinalPrice) + getString(R.string.le));

        hideLoading();
    }

    private void showError(String msg) {
        hideLoading();
        Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
    }

    // -----------------------------
    // HELPERS
    // -----------------------------
    public String getFormattedPrice() {
        return String.format("%.2f", totalPrice);
    }

    // Called when items change (remove or update)
    public void updateAfterChange(double amount, char type) {
        if (type == '+') totalPrice += amount;
        else totalPrice -= amount;

        updateSmallUI();
    }

    public void removeItem(double amount, String docId) {
        items--;
        totalPrice -= amount;
        updateSmallUI();
        totalData.remove(docId);

        if (items == 0)
            binding.btnBack.performClick();
    }


    private void updateSmallUI() {
        binding.noOfItems.setText(items + (items == 1 ? getString(R.string.item) : getString(R.string.items)));
        double totalFinalPrice = Double.parseDouble(normalizeNumber(String.valueOf(getFormattedPrice()))) + 20.00;
        amountInPiasters = (long) (totalFinalPrice * 100); // Stripe amount in cents
        binding.subTotalPriceOfItemsPayment.setText(getFormattedPrice() + getString(R.string.le));
        binding.totalPriceOfItemsPayment.setText(String.format("%.2f", totalFinalPrice) + getString(R.string.le));
    }

    private void createPaymentIntent(long amount) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("amount", amount); // amount in piasters
        } catch (JSONException e) {
            binding.btnPayment.setEnabled(true);
            binding.btnPayment.setText(R.string.payment);
            binding.progressBar.setVisibility(View.GONE);
            Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
        }

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toString());

//        String url = "http://10.0.2.2:5000/create_payment_intent"; // emulator device
        String url = "http://192.168.1.100:5000/create_payment_intent"; // real device

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    binding.btnPayment.setEnabled(true);
                    binding.btnPayment.setText(R.string.payment);
                    binding.progressBar.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), "Payment error: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject jsonResponse = new JSONObject(res);
                    if (jsonResponse.has("clientSecret")) {
                        clientSecret = jsonResponse.getString("clientSecret");
                        runOnUiThread(() -> presentPaymentSheet());
                    } else {
                        String error = jsonResponse.optString("error", "Unknown error");
                        runOnUiThread(() -> {
                            binding.btnPayment.setEnabled(true);
                            binding.btnPayment.setText(R.string.payment);
                            binding.progressBar.setVisibility(View.GONE);
                            Snackbar.make(binding.getRoot(), "Error: " + error, Snackbar.LENGTH_LONG).show();
                        });
                    }
                } catch (JSONException e) {
                    binding.btnPayment.setEnabled(true);
                    binding.btnPayment.setText(R.string.payment);
                    binding.progressBar.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void presentPaymentSheet() {
        PaymentSheet.Configuration config = new PaymentSheet.Configuration("Winkel App");
        paymentSheet.presentWithPaymentIntent(clientSecret, config);
    }

    private void onPaymentResult(@NonNull PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            saveOrderToFirestore();
        } else if (result instanceof PaymentSheetResult.Canceled) {
            binding.btnPayment.setEnabled(true);
            binding.btnPayment.setText(R.string.payment);
            binding.progressBar.setVisibility(View.GONE);
            Snackbar.make(binding.getRoot(), "Payment canceled", Snackbar.LENGTH_SHORT).show();
        } else if (result instanceof PaymentSheetResult.Failed) {
            binding.btnPayment.setEnabled(true);
            binding.btnPayment.setText(R.string.payment);
            binding.progressBar.setVisibility(View.GONE);
            Snackbar.make(binding.getRoot(), "Payment failed: " + ((PaymentSheetResult.Failed) result).getError().getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void saveOrderToFirestore() {

        String orderId = firestore.collection("Orders").document().getId();

        DocumentReference orderRef =
                firestore.collection("Orders").document(orderId);

        Map<String, Object> order = new HashMap<>();
        order.put("userId", userId);
        order.put("totalPrice", totalPrice);
        order.put("deliveryFee", 20.0);
        order.put("status", "PAID");
        order.put("createdAt", FieldValue.serverTimestamp());

        Map<String, Object> address = new HashMap<>();
        address.put("state", binding.addressState.getText().toString());
        address.put("city", binding.addressCity.getText().toString());
        address.put("street", binding.addressStreet.getText().toString());
        order.put("address", address);

        WriteBatch batch = firestore.batch();
        batch.set(orderRef, order);

        for (String itemId : totalData.keySet()) {
            String[] p = totalData.get(itemId).split(",");

            Map<String, Object> item = new HashMap<>();
            item.put("itemId", itemId);
            item.put("itemType", p[1]);
            item.put("quantity", Long.parseLong(p[2]));
            item.put("price", Double.parseDouble(p[3]));
            item.put("size", p[4]);

            batch.set(orderRef.collection("items").document(itemId), item);
        }

        // CLEAR NEW BASKET
        firestore.collection("UsersData")
                .document(userId)
                .collection("Basket")
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot d : query) {
                        batch.delete(d.getReference());
                    }

                    batch.commit().addOnSuccessListener(a -> {
                        Snackbar.make(binding.getRoot(),
                                R.string.payment_successful,
                                Snackbar.LENGTH_SHORT).show();

                        Intent home = new Intent(this, MainActivity.class);
                        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(home);
                        finish();
                    });
                });
    }


    private void checkAddressFields() {
        isAllAddressAdded = !binding.addressState.getText().toString().trim().isEmpty() && !binding.addressCity.getText().toString().trim().isEmpty() && !binding.addressStreet.getText().toString().trim().isEmpty();
    }

    private String normalizeNumber(String value) {
        if (value == null) return "0";
        return value.replace("٠", "0").replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5").replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9").replace("٫", ".");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
