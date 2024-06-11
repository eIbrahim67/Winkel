package com.eibrahim.winkel.secondPages.paymentPages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataPaymentMethodItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class addPaymentMethodActivity extends AppCompatActivity {

    String type = "unValid";

    TextView holder_name_new, cvv_new, number_new;
    RelativeLayout card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        EditText number = findViewById(R.id.number);
        EditText date = findViewById(R.id.date);
        EditText holder_name = findViewById(R.id.holder_name);
        Button addMethod = findViewById(R.id.addMethod);

        type = getIntent().getStringExtra("type");

        if (Objects.equals(type, "visa")){
            holder_name_new = findViewById(R.id.holder_name_visa_new);
            cvv_new = findViewById(R.id.cvv_visa_new);
            number_new = findViewById(R.id.number_visa_new);

            card = findViewById(R.id.visa_new);
        }
        else if (Objects.equals(type, "mastercard")){
            holder_name_new = findViewById(R.id.holder_name_mastercard_new);
            cvv_new = findViewById(R.id.cvv_mastercard_new);
            number_new = findViewById(R.id.number_mastercard_new);

            card = findViewById(R.id.mastercard_new);
        }

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                number_new.setText(s.toString());

            }
        });

        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                cvv_new.setText(s.toString());

            }
        });

        holder_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                holder_name_new.setText(s.toString());

            }
        });

        card.setVisibility(View.VISIBLE);

        addMethod.setOnClickListener(v -> {

            DataPaymentMethodItem itemOfMethod = new DataPaymentMethodItem(

                    holder_name.getText().toString(),
                    type,
                    number.getText().toString(),
                    date.getText().toString()

            );

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference collectionRef = firestore.collection("UsersData").
                    document(userId).collection("PaymentMethodsCollection");
            DocumentReference documentRef = collectionRef.document();
            String documentId = documentRef.getId();
            collectionRef.document(documentId).set(itemOfMethod)
                    .addOnSuccessListener(aVoid -> Toast.makeText(addPaymentMethodActivity.this, getString(R.string.payment_added_success), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(addPaymentMethodActivity.this, getString(R.string.payment_add_failed), Toast.LENGTH_SHORT).show());


        });
        
    }
}