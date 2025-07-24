package com.eibrahim.winkel._old._payment.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ActivityPaymentBinding;
import com.eibrahim.winkel.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    private boolean isAllAddressAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            String totalPrice = getIntent().getStringExtra("Total price");
            String totalData = getIntent().getStringExtra("Data");
            double deliveryCost = 5.00;

            binding.subTotalPriceOfItemsPayment.setText(totalPrice + getString(R.string.le));
            binding.deliveryCostPayment.setText(String.format("%.2f", deliveryCost) + getString(R.string.le));

            double totalFinalPrice = Double.parseDouble(Objects.requireNonNull(totalPrice)) + deliveryCost;
            binding.totalPriceOfItemsPayment.setText(String.format("%.2f", totalFinalPrice) + getString(R.string.le));

            // Address field watcher
            TextWatcher addressWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkAddressFields();
                }
            };

            binding.addressCountry.addTextChangedListener(addressWatcher);
            binding.addressState.addTextChangedListener(addressWatcher);
            binding.addressCity.addTextChangedListener(addressWatcher);
            binding.addressArea.addTextChangedListener(addressWatcher);
            binding.addressStreet.addTextChangedListener(addressWatcher);

            binding.btnBackCheckout.setOnClickListener(v -> finish());

            DocumentReference basketRef = firestore.collection("UsersData")
                    .document(userId)
                    .collection("BasketCollection")
                    .document("BasketDocument");

            binding.btnPayment.setOnClickListener(v -> {
                try {
                    if (isAllAddressAdded) {
                        DocumentReference orderRef = firestore.collection("Orders").document(userId);
                        orderRef.update("OrderCollection", FieldValue.arrayUnion(totalData))
                                .addOnSuccessListener(unused -> {
                                    basketRef.update("BasketCollection", FieldValue.delete());
                                    Toast.makeText(this, getString(R.string.payment_successful), Toast.LENGTH_SHORT).show();
                                    Intent intentHome = new Intent(PaymentActivity.this, MainActivity.class);
                                    intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intentHome);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    Toast.makeText(this, getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Please complete all address details before proceeding.", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAddressFields() {
        boolean allFilled = !binding.addressCountry.getText().toString().trim().isEmpty() &&
                !binding.addressState.getText().toString().trim().isEmpty() &&
                !binding.addressCity.getText().toString().trim().isEmpty() &&
                !binding.addressArea.getText().toString().trim().isEmpty() &&
                !binding.addressStreet.getText().toString().trim().isEmpty();

        isAllAddressAdded = allFilled;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
