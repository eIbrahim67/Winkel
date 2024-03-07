package com.eibrahim.winkel.secondActivity;


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
import com.eibrahim.winkel.mianActivity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PaymentActivity extends AppCompatActivity {

    TextView TotalPriceOfItems, noOfItems;
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
        double deliveryCost = 5.00;

        TotalPriceOfItemsCheckout.setText("$" + totalPrice);
        DeliveryCostPayment.setText("$" + String.format("%.2f", deliveryCost));
        TotalPriceOfItemsPayment.setText("$" + String.valueOf(Double.parseDouble(totalPrice) + deliveryCost));

        fetchPaymentMethodsData(recyclerView_payment, this);

        btnBackCheckout.setOnClickListener(v -> finish());

        btnPayment.setOnClickListener(v -> {
            if (donePayment){
                Toast.makeText(PaymentActivity.this, "Payment is DONE", Toast.LENGTH_SHORT).show();
                Intent intentHomeActivity = new Intent(PaymentActivity.this, MainActivity.class);
                intentHomeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentHomeActivity);
                finish();
            }else
                Toast.makeText(PaymentActivity.this, "Choose your payment method", Toast.LENGTH_SHORT).show();

        });
    }

    private void fetchPaymentMethodsData(RecyclerView recyclerView, Context context) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = String.valueOf(auth.getCurrentUser().getUid());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        List<dataRecyclerviewPaymentMethods> dataOfRvItems = new ArrayList<>();

        CollectionReference collectionRef = firestore.collection("UsersData")
                .document(userId).collection("PaymentMethodsCollection");
        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Get the document data
                        Map<String, Object> data = document.getData();

                        dataRecyclerviewPaymentMethods dataObject = new dataRecyclerviewPaymentMethods(
                                (String) data.get("type"),
                                (String) data.get("number"),
                                (String) data.get("date")
                        );
                        dataOfRvItems.add(dataObject);
                    }
                    adapterRecyclerviewPaymentMethods adapterRvItems = new adapterRecyclerviewPaymentMethods(context, dataOfRvItems, PaymentActivity.this);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    recyclerView.setAdapter(adapterRvItems);
                })
                .addOnFailureListener(e -> {
                });
    }

    public void donePayment(boolean state){
        donePayment = state;
    }

    private boolean donePayment = false;
}