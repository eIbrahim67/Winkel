package com.eibrahim.winkel.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityPaymentBinding;
import com.eibrahim.winkel.main.LocaleHelper;
import com.eibrahim.winkel.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
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

    private ActivityPaymentBinding binding;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    private boolean isAllAddressAdded = false;

    private PaymentSheet paymentSheet;
    private String clientSecret;
    private String totalData;

    private ProgressDialog progressDialog;

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
            AppCompatDelegate.setDefaultNightMode(
                    isDarkMode == 1 ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        super.onCreate(savedInstanceState);

        // Initialize Stripe
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51SVFRGFpTKioic1Y7QUBbLvSCsKYzOa5eIkMaTMHy7XBZ32qHUK2SlferifioQ1LBELzqOdOO26aovGefEYwONpn00WTwiPQmQ"
        );

        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Stripe PaymentSheet init
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        totalData = getIntent().getStringExtra("Data");
        String totalPrice = getIntent().getStringExtra("Total price");

        double deliveryCost = 20.00;
        double totalFinalPrice = Double.parseDouble(normalizeNumber(totalPrice)) + deliveryCost;
        long amountInPiasters = (long) (totalFinalPrice * 100); // Stripe amount in cents

        binding.subTotalPriceOfItemsPayment.setText(totalPrice + getString(R.string.le));
        binding.deliveryCostPayment.setText(String.format("%.2f", deliveryCost) + getString(R.string.le));
        binding.totalPriceOfItemsPayment.setText(String.format("%.2f", totalFinalPrice) + getString(R.string.le));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing payment...");
        progressDialog.setCancelable(false);

        // Address TextWatchers
        TextWatcher addressWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAddressFields();
            }
        };
        binding.addressState.addTextChangedListener(addressWatcher);
        binding.addressCity.addTextChangedListener(addressWatcher);
        binding.addressStreet.addTextChangedListener(addressWatcher);

        binding.btnBackCheckout.setOnClickListener(v -> finish());

        // Payment button click
        binding.btnPayment.setOnClickListener(v -> {
            if (!isAllAddressAdded) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please complete all address details before proceeding.",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }
            createPaymentIntent(amountInPiasters);
        });
    }

    private void createPaymentIntent(long amount) {
        progressDialog.show();

        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        try {
            json.put("amount", amount); // amount in piasters
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toString());

        // Use emulator IP for localhost or your PC IP for device
        String url = "http://10.0.2.2:5000/create_payment_intent";

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(PaymentActivity.this, "Payment error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("PaymentError", e.toString());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(res);
                    if (jsonResponse.has("clientSecret")) {
                        clientSecret = jsonResponse.getString("clientSecret");
                        runOnUiThread(() -> presentPaymentSheet());
                    } else {
                        String error = jsonResponse.optString("error", "Unknown error");
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(PaymentActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            Log.e("PaymentError", error);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void presentPaymentSheet() {
        progressDialog.dismiss();
        PaymentSheet.Configuration config = new PaymentSheet.Configuration("Winkel App");
        paymentSheet.presentWithPaymentIntent(clientSecret, config);
    }

    private void onPaymentResult(@NonNull PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            saveOrderToFirestore();
        } else if (result instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
        } else if (result instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this,
                    "Payment failed: " + ((PaymentSheetResult.Failed) result).getError().getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void saveOrderToFirestore() {
        DocumentReference basketRef = firestore.collection("UsersData")
                .document(userId)
                .collection("BasketCollection")
                .document("BasketDocument");

        DocumentReference orderRef = firestore.collection("Orders").document(userId);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("OrderCollection", FieldValue.arrayUnion(totalData));

        orderRef.set(updateData, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    basketRef.update("BasketCollection", FieldValue.delete());
                    Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();

                    Intent home = new Intent(PaymentActivity.this, MainActivity.class);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(home);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Order save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkAddressFields() {
        isAllAddressAdded = !binding.addressState.getText().toString().trim().isEmpty()
                && !binding.addressCity.getText().toString().trim().isEmpty()
                && !binding.addressStreet.getText().toString().trim().isEmpty();
    }

    private String normalizeNumber(String value) {
        if (value == null) return "0";
        return value.replace("٠", "0").replace("١", "1").replace("٢", "2")
                .replace("٣", "3").replace("٤", "4").replace("٥", "5")
                .replace("٦", "6").replace("٧", "7").replace("٨", "8")
                .replace("٩", "9").replace("٫", ".");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
