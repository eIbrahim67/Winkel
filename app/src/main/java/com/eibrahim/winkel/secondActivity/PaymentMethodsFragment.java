package com.eibrahim.winkel.secondActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.dataRecyclerviewPaymentMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PaymentMethodsFragment extends Fragment {


    public PaymentMethodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment_methods, container, false);

        EditText type = rootView.findViewById(R.id.type);
        EditText number = rootView.findViewById(R.id.number);
        EditText date = rootView.findViewById(R.id.date);
        Button addMethod = rootView.findViewById(R.id.addMethod);

        addMethod.setOnClickListener(v -> {

            dataRecyclerviewPaymentMethods itemOfMethod = new dataRecyclerviewPaymentMethods(

                    type.getText().toString(),
                    number.getText().toString(),
                    date.getText().toString()

            );

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userId = String.valueOf(Objects.requireNonNull(auth.getCurrentUser()).getUid());

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference collectionRef = firestore.collection("UsersData").
                    document(userId).collection("PaymentMethodsCollection");
            DocumentReference documentRef = collectionRef.document();
            String documentId = documentRef.getId();
            collectionRef.document(documentId).set(itemOfMethod)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Payment method added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));

        });

        return rootView;
    }
}