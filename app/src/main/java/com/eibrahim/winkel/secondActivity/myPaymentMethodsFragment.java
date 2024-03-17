package com.eibrahim.winkel.secondActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewPaymentMethods;
import com.eibrahim.winkel.bottomSheets.addMethodBottomSheet;
import com.eibrahim.winkel.dataClasses.dataRecyclerviewPaymentMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class myPaymentMethodsFragment extends Fragment {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_payment_methods, container, false);

        RecyclerView recyclerview_show_methods = root.findViewById(R.id.recyclerview_show_methods);
        Button btnAddNewMethod = root.findViewById(R.id.btnAddNewMethod);
        addMethodBottomSheet addMethodBottomSheet = new addMethodBottomSheet(requireContext());

        SwipeRefreshLayout myPaymentMethodsFragment_layout = root.findViewById(R.id.myPaymentMethodsFragment_layout);

        myPaymentMethodsFragment_layout.setOnRefreshListener(() -> {
            fetchPaymentMethodsData(recyclerview_show_methods, requireContext());
            myPaymentMethodsFragment_layout.setRefreshing(false);
        }
        );

        fetchPaymentMethodsData(recyclerview_show_methods, requireContext());

        btnAddNewMethod.setOnClickListener(v-> addMethodBottomSheet.show(requireActivity().getSupportFragmentManager(), ""));

        return root;
    }

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

                        dataRecyclerviewPaymentMethods dataObject = new dataRecyclerviewPaymentMethods(
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