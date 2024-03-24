package com.eibrahim.winkel.mainPages;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewFilter;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewItems;
import com.eibrahim.winkel.bottomSheets.functionsBottomSheet;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.eibrahim.winkel.declaredClasses.FetchDataFromFirebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView_filter;
    private RecyclerView recyclerView_items;
    public static View recyclerViewItemsMens_skeleton, recyclerViewItemsWomen_skeleton, recyclerViewItemsKids_skeleton, recyclerViewItemsOffers_skeleton;
    private TextView btnItemsOffers;
    private RelativeLayout btnItemsMens, btnItemsWomen, btnItemsKids;
    private SwipeRefreshLayout fragment_home;
    private ScrollView ScrollView_of_ItemsTypes;
    private FetchDataFromFirebase fetchDataFromFirebase;
    private Boolean filtered = false;
    private String type, fPrice, tPrice;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private LinearLayout search_page, search_btn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ScrollView_of_ItemsTypes = root.findViewById(R.id.ScrollView_of_ItemsTypes);

        recyclerView_filter = root.findViewById(R.id.recyclerview_filter);
        recyclerView_items = root.findViewById(R.id.recyclerview_items);
        RecyclerView recyclerViewItemsMens = root.findViewById(R.id.recyclerviewItemsMens);
        RecyclerView recyclerViewItemsWomen = root.findViewById(R.id.recyclerviewItemsWomen);
        RecyclerView recyclerViewItemsKids = root.findViewById(R.id.recyclerviewItemsKids);
        RecyclerView recyclerViewItemsOffers = root.findViewById(R.id.recyclerviewItemsOffers);
        RecyclerView recyclerview_search = root.findViewById(R.id.recyclerview_search);


        //recyclerView_items_skeleton = root.findViewById(R.id.recyclerview_items_skeleton);
        recyclerViewItemsMens_skeleton = root.findViewById(R.id.recyclerviewItemsMens_skeleton);
        recyclerViewItemsWomen_skeleton = root.findViewById(R.id.recyclerviewItemsWomen_skeleton);
        recyclerViewItemsKids_skeleton = root.findViewById(R.id.recyclerviewItemsKids_skeleton);
        recyclerViewItemsOffers_skeleton = root.findViewById(R.id.recyclerviewItemsOffers_skeleton);


        btnItemsMens = root.findViewById(R.id.btnItemsMens);
        btnItemsWomen = root.findViewById(R.id.btnItemsWomen);
        btnItemsKids = root.findViewById(R.id.btnItemsKids);
        btnItemsOffers = root.findViewById(R.id.btnItemsOffers);

        fragment_home = root.findViewById(R.id.fragment_home);

        search_btn = root.findViewById(R.id.search_btn);
        search_page = root.findViewById(R.id.search_page);

        ImageView btnFilterH = root.findViewById(R.id.btnFunctions);
        functionsBottomSheet functionsBottomSheet = new functionsBottomSheet(HomeFragment.this);
        EditText search_text = root.findViewById(R.id.search_text);


        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView_items,
                recyclerViewItemsMens,
                recyclerViewItemsWomen,
                recyclerViewItemsKids,
                recyclerViewItemsOffers,
                requireContext()
        );

        fetchData();

        search_btn.setOnClickListener(v -> {

            search_page.setVisibility(View.VISIBLE);
            MainActivity.chipNavigationBar.setVisibility(View.GONE);
            search_text.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(search_text, InputMethodManager.SHOW_IMPLICIT);
        });

        btnItemsMens.setOnClickListener(v -> {

            doFilter("Mens", "0", "1000");

        });

        btnItemsWomen.setOnClickListener(v -> {

            doFilter("Womens", "0", "1000");

        });

        btnItemsKids.setOnClickListener(v -> {

            doFilter("Kids", "0", "1000");

        });

        btnItemsOffers.setOnClickListener(v -> {

            doFilter("Offers", "0", "1000");

        });

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                if (!searchText.isEmpty()) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    CollectionReference collectionRef = firestore.collection("Products").document("Mens").collection("Mens");
                    collectionRef
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<DataRecyclerviewMyItem> dataOfRvItems = new ArrayList<>();
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Map<String, Object> data = document.getData();
                                    String itemName = (String) data.get("name");

                                    // Perform case-insensitive partial string match
                                    if (itemName.toLowerCase().contains(searchText.toLowerCase())) {
                                        DataRecyclerviewMyItem dataObject = new DataRecyclerviewMyItem(
                                                (String) data.get("category"),
                                                (String) data.get("imageId"),
                                                itemName,
                                                (String) data.get("price"),
                                                "Mens",
                                                ""
                                        );

                                        dataObject.setItemId((String) data.get("itemId"));
                                        dataObject.setItemLoved(false);

                                        dataOfRvItems.add(dataObject);
                                    }
                                }


                                adapterRecyclerviewItems adapterRvItems = new  adapterRecyclerviewItems(requireContext(), dataOfRvItems, "Mens");
                                recyclerview_search.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                                recyclerview_search.setAdapter(adapterRvItems);


                            })
                            .addOnFailureListener(e -> Log.d("Firestore", "Error getting documents", e));
                } else {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnFilterH.setOnClickListener(v -> {


            if (functionsBottomSheet.isVisible())
                functionsBottomSheet.dismiss();
            else
                functionsBottomSheet.show(requireActivity().getSupportFragmentManager(), "");

        });

        fragment_home.setOnRefreshListener(() -> {

            if (filtered)
                doFilter(type, fPrice, tPrice);
            else
                fetchData();

            fragment_home.setRefreshing(false);

        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (search_page.getVisibility() == View.VISIBLE) {

                    search_page.setVisibility(View.GONE);
                    MainActivity.chipNavigationBar.setVisibility(View.VISIBLE);

                } else {
                    requireActivity().moveTaskToBack(true);
                }
            }
        });
    }



    private void fetchData(){

        HomeFragment.recyclerViewItemsMens_skeleton.setVisibility(View.VISIBLE);
        HomeFragment.recyclerViewItemsWomen_skeleton.setVisibility(View.VISIBLE);
        HomeFragment.recyclerViewItemsKids_skeleton.setVisibility(View.VISIBLE);
        HomeFragment.recyclerViewItemsOffers_skeleton.setVisibility(View.VISIBLE);

        fetchDataFromFirebase.fetchData("All", "0", "100000", 1, recyclerView_items);

    }

    public void recyclerviewVisibility(String type){

        switch (type) {
            case "All":
                btnItemsMens.setVisibility(View.VISIBLE);
                btnItemsWomen.setVisibility(View.VISIBLE);
                btnItemsKids.setVisibility(View.VISIBLE);
                btnItemsOffers.setVisibility(View.VISIBLE);
                recyclerView_filter.setVisibility(View.GONE);
                ScrollView_of_ItemsTypes.setVisibility(View.VISIBLE);
                filtered = false;

                break;
            case "Mens":
            case "Womens":
            case "Kids":
                btnItemsMens.setVisibility(View.GONE);
                btnItemsWomen.setVisibility(View.GONE);
                btnItemsKids.setVisibility(View.GONE);
                btnItemsOffers.setVisibility(View.GONE);
                ScrollView_of_ItemsTypes.setVisibility(View.GONE);
                recyclerView_filter.setVisibility(View.VISIBLE);
                filtered = true;
                break;
        }
    }

    public void doFilter(String type, String fPrice, String tPrice){

        this.type = type;
        this.fPrice = fPrice;
        this.tPrice = tPrice;

        fetchDataFromFirebase.fetchData(type, fPrice, tPrice, 1, recyclerView_items);
        fetchCategory(type, fPrice, tPrice);

    }

    private void fetchCategory(String type, String fPrice, String tPrice){

        recyclerviewVisibility(type);

        if(type.equals("All")){

            return;
        }

        List<String> dataOfRvFilter = new ArrayList<>();

        DocumentReference docReference = firestore.collection("Data").document("Categories" + type);

        docReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Object> data = (List<Object>) documentSnapshot.get("Categories");
                for (Object item : Objects.requireNonNull(data)) {
                    dataOfRvFilter.add(item.toString());
                }
            }
            adapterRecyclerviewFilter adapterRvFilter = new adapterRecyclerviewFilter(dataOfRvFilter, recyclerView_items, type,fPrice, tPrice, requireContext());
            recyclerView_filter.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView_filter.setAdapter(adapterRvFilter);
        }).addOnFailureListener(e -> {
        });
    }

}