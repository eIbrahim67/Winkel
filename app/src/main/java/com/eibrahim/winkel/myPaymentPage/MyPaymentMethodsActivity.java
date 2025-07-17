package com.eibrahim.winkel.myPaymentPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Button;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewPaymentMethods;
import com.eibrahim.winkel.bottomSheets.addMethodBottomSheet;
import com.eibrahim.winkel.dataClasses.DataPaymentMethodItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyPaymentMethodsActivity extends AppCompatActivity {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private final String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_payment_methods);

        RecyclerView recyclerview_show_methods = findViewById(R.id.recyclerview_show_methods);
        Button btnAddNewMethod = findViewById(R.id.btnAddNewMethod);
        com.eibrahim.winkel.bottomSheets.addMethodBottomSheet addMethodBottomSheet = new addMethodBottomSheet(this);

        SwipeRefreshLayout myPaymentMethodsFragment_layout = findViewById(R.id.myPaymentMethodsFragment_layout);

        myPaymentMethodsFragment_layout.setOnRefreshListener(() -> {
                    fetchPaymentMethodsData(recyclerview_show_methods);
                    myPaymentMethodsFragment_layout.setRefreshing(false);
                }
        );

        fetchPaymentMethodsData(recyclerview_show_methods);

        btnAddNewMethod.setOnClickListener(v -> addMethodBottomSheet.show(getSupportFragmentManager(), ""));

    }

    private void fetchPaymentMethodsData(RecyclerView recyclerView) {

        List<DataPaymentMethodItem> dataOfRvItems = new ArrayList<>();

        CollectionReference collectionRef = firestore.collection("UsersData")
                .document(userId).collection("PaymentMethodsCollection");
        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Get the document data
                        Map<String, Object> data = document.getData();

                        DataPaymentMethodItem dataObject = new DataPaymentMethodItem(
                                (String) Objects.requireNonNull(data).get("holder_name"),
                                (String) data.get("type"),
                                (String) data.get("number"),
                                (String) data.get("cvv")
                        );
                        dataOfRvItems.add(dataObject);
                    }
                    adapterRecyclerviewPaymentMethods adapterRvItems = new adapterRecyclerviewPaymentMethods(this, dataOfRvItems);
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
                    recyclerView.setAdapter(adapterRvItems);
                })
                .addOnFailureListener(e -> {
                });
    }
}