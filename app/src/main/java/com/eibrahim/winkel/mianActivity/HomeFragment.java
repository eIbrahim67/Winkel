package com.eibrahim.winkel.mianActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewFilter;
import com.eibrahim.winkel.bottomSheets.FilterBottomSheet;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.declaredClasses.FetchDataFromFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    public static final List<String> wishlistIds = new ArrayList<>();
    private RecyclerView recyclerView_filter, recyclerView_items, recyclerViewItemsMens, recyclerViewItemsWomens, recyclerViewItemsKids, recyclerViewItemsOffers;
    public static View recyclerViewItemsMens_skeleton, recyclerViewItemsWomens_skeleton, recyclerViewItemsKids_skeleton, recyclerViewItemsOffers_skeleton;
    public static LinearLayout recyclerView_items_skeleton;

    private TextView btnItemsMens, btnItemsWomen, btnItemsKids, btnItemsOffers;

    private SwipeRefreshLayout fragment_home;

    private ScrollView ScrollView_of_ItemsTypes;

    private Boolean filtered = false;
    private String type, fPricee, tPricee;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ScrollView_of_ItemsTypes = root.findViewById(R.id.ScrollView_of_ItemsTypes);

        recyclerView_filter = root.findViewById(R.id.recyclerview_filter);
        recyclerView_items = root.findViewById(R.id.recyclerview_items);
        recyclerViewItemsMens = root.findViewById(R.id.recyclerviewItemsMens);
        recyclerViewItemsWomens = root.findViewById(R.id.recyclerviewItemsWomens);
        recyclerViewItemsKids = root.findViewById(R.id.recyclerviewItemsKids);
        recyclerViewItemsOffers = root.findViewById(R.id.recyclerviewItemsOffers);

        //recyclerView_items_skeleton = root.findViewById(R.id.recyclerview_items_skeleton);
        recyclerViewItemsMens_skeleton = root.findViewById(R.id.recyclerviewItemsMens_skeleton);
        recyclerViewItemsWomens_skeleton = root.findViewById(R.id.recyclerviewItemsWomens_skeleton);
        recyclerViewItemsKids_skeleton = root.findViewById(R.id.recyclerviewItemsKids_skeleton);
        recyclerViewItemsOffers_skeleton = root.findViewById(R.id.recyclerviewItemsOffers_skeleton);


        btnItemsMens = root.findViewById(R.id.btnItemsMens);
        btnItemsWomen = root.findViewById(R.id.btnItemsWomen);
        btnItemsKids = root.findViewById(R.id.btnItemsKids);
        btnItemsOffers = root.findViewById(R.id.btnItemsOffers);

        fragment_home = root.findViewById(R.id.fragment_home);

        ImageView btnFilterH = root.findViewById(R.id.btnFilterH);

        fetchData();

        btnFilterH.setOnClickListener(v -> {

            FilterBottomSheet filterBottomSheet = new FilterBottomSheet(HomeFragment.this);
            filterBottomSheet.show(requireActivity().getSupportFragmentManager(), "");


        });

        fragment_home.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (filtered)
                    doFilter(type, fPricee, tPricee);
                else
                    fetchData();

                fragment_home.setRefreshing(false);


            }
        });

        return root;
    }

    private void fetchData(){

        HomeFragment.recyclerViewItemsMens_skeleton.setVisibility(View.VISIBLE);
        HomeFragment.recyclerViewItemsWomens_skeleton.setVisibility(View.VISIBLE);
        HomeFragment.recyclerViewItemsKids_skeleton.setVisibility(View.VISIBLE);
        HomeFragment.recyclerViewItemsOffers_skeleton.setVisibility(View.VISIBLE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        CollectionReference collectionRef = firestore.collection("UsersData")
                .document(userId).collection("WishlistCollection");
        collectionRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String documentId = document.getId();
                        wishlistIds.add(documentId);
                    }
                    FetchDataFromFirebase fetchDataFromFirebase = new FetchDataFromFirebase(
                            recyclerView_items,
                            recyclerViewItemsMens,
                            recyclerViewItemsWomens,
                            recyclerViewItemsKids,
                            recyclerViewItemsOffers,
                            requireContext()
                    );
                    fetchDataFromFirebase.fetchData("All", "0", "100000", 1, recyclerView_items);
                })
                .addOnFailureListener(e -> {
                });


    }

    public void recyclerviewVisis(String type){

        if(type.equals("All")) {
            btnItemsMens.setVisibility(View.VISIBLE);
            btnItemsWomen.setVisibility(View.VISIBLE);
            btnItemsKids.setVisibility(View.VISIBLE);
            btnItemsOffers.setVisibility(View.VISIBLE);
            recyclerView_filter.setVisibility(View.GONE);
            ScrollView_of_ItemsTypes.setVisibility(View.VISIBLE);
            filtered = false;

        }

        else if(type.equals("Mens")) {

            btnItemsMens.setVisibility(View.GONE);
            btnItemsWomen.setVisibility(View.GONE);
            btnItemsKids.setVisibility(View.GONE);
            btnItemsOffers.setVisibility(View.GONE);
            ScrollView_of_ItemsTypes.setVisibility(View.GONE);
            recyclerView_filter.setVisibility(View.VISIBLE);
            filtered = true;
        }

        else if(type.equals("Women")) {
            btnItemsMens.setVisibility(View.GONE);
            btnItemsWomen.setVisibility(View.GONE);
            btnItemsKids.setVisibility(View.GONE);
            btnItemsOffers.setVisibility(View.GONE);
            ScrollView_of_ItemsTypes.setVisibility(View.GONE);
            recyclerView_filter.setVisibility(View.VISIBLE);
            filtered = true;
        }


        else if(type.equals("Kids")) {
            btnItemsMens.setVisibility(View.GONE);
            btnItemsWomen.setVisibility(View.GONE);
            btnItemsKids.setVisibility(View.GONE);
            btnItemsOffers.setVisibility(View.GONE);
            ScrollView_of_ItemsTypes.setVisibility(View.GONE);
            recyclerView_filter.setVisibility(View.VISIBLE);
            filtered = true;
        }
    }

    public void doFilter(String type, String fPricee, String tPricee){

        this.type = type;
        this.fPricee = fPricee;
        this.tPricee = tPricee;

        FetchDataFromFirebase fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView_items,
                recyclerViewItemsMens,
                recyclerViewItemsWomens,
                recyclerViewItemsKids,
                recyclerViewItemsOffers,
                requireContext()
        );
        fetchDataFromFirebase.fetchData(type, fPricee, tPricee, 1, recyclerView_items);
        fetchCategory(type, fPricee, tPricee);

    }

    private void fetchCategory(String type, String fPricee, String tPricee){

        recyclerviewVisis(type);

        if(type.equals("All")){

            return;
        }

        List<String> dataOfRvFilter = new ArrayList<>();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docReference = firestore.collection("Data").document("Categories" + type);

        docReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Object> data = (List<Object>) documentSnapshot.get("Categories");
                for (Object item : data) {
                    dataOfRvFilter.add(item.toString());
                }
            }
            adapterRecyclerviewFilter adapterRvFilter = new adapterRecyclerviewFilter(dataOfRvFilter, recyclerView_items, type,fPricee, tPricee, requireContext());
            recyclerView_filter.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView_filter.setAdapter(adapterRvFilter);
        }).addOnFailureListener(e -> {
        });
    }

}