package com.eibrahim.winkel.secondPages;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewPaymentMethods;
import com.eibrahim.winkel.dataClasses.dataRecyclerviewPaymentMethods;
import com.eibrahim.winkel.mainPages.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PaymentActivity extends AppCompatActivity {

    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        RecyclerView recyclerView_payment = findViewById(R.id.rv4);
        TextView TotalPriceOfItemsCheckout = findViewById(R.id.subTotalPriceOfItemsPayment);
        TextView DeliveryCostPayment = findViewById(R.id.DeliveryCostPayment);
        TextView TotalPriceOfItemsPayment = findViewById(R.id.TotalPriceOfItemsPayment);
        ImageView btnBackCheckout = findViewById(R.id.btnBackCheckout);
        Button btnPayment = findViewById(R.id.btnPayment);
        String totalPrice = getIntent().getStringExtra("Total price");
        String totalData = getIntent().getStringExtra("Data");

        double deliveryCost = 5.00;

        String temp = totalPrice + getString(R.string.le);
        TotalPriceOfItemsCheckout.setText(temp);
        temp = String.format("%.2f", deliveryCost) + getString(R.string.le);
        DeliveryCostPayment.setText(temp);
        temp = Double.parseDouble(Objects.requireNonNull(totalPrice)) + deliveryCost + getString(R.string.le);
        TotalPriceOfItemsPayment.setText(temp);

        fetchPaymentMethodsData(recyclerView_payment, this);

        btnBackCheckout.setOnClickListener(v -> finish());

        DocumentReference basketRef = firestore.collection("UsersData")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("BasketCollection")
                .document("BasketDocument");

        btnPayment.setOnClickListener(v -> {
            //TODO: use donePayment instead of true
            DocumentReference orderRef = firestore.collection("Orders")
                    .document(userId);

            orderRef
                    .update("OrderCollection", FieldValue.arrayUnion(totalData))
                    .addOnSuccessListener(unused -> {

                        basketRef.update("BasketCollection", FieldValue.delete());

                        Toast.makeText(PaymentActivity.this, "Payment successful.", Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> Toast.makeText(PaymentActivity.this, "An unexpected error occurred. Please try again later.", Toast.LENGTH_SHORT).show());

            Intent intentHomeActivity = new Intent(PaymentActivity.this, MainActivity.class);
            intentHomeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentHomeActivity);
            finish();

        });
    }
    dataRecyclerviewPaymentMethods dataObject;
    private void fetchPaymentMethodsData(RecyclerView recyclerView, Context context) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<dataRecyclerviewPaymentMethods> dataOfRvItems = new ArrayList<>();

        CollectionReference collectionRef = firestore.collection("UsersData")
                .document(userId).collection("PaymentMethodsCollection");
        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Get the document data
                        Map<String, Object> data = document.getData();

                        dataObject = new dataRecyclerviewPaymentMethods(
                                (String) Objects.requireNonNull(data).get("holder_name"),
                                (String) data.get("type"),
                                (String) data.get("number"),
                                (String) data.get("cvv")
                        );
                        dataOfRvItems.add(dataObject);
                    }
                    adapterRecyclerviewPaymentMethods adapterRvItems = new adapterRecyclerviewPaymentMethods(context, dataOfRvItems);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    recyclerView.setAdapter(adapterRvItems);
                })
                .addOnFailureListener(e -> {
                });
    }

}