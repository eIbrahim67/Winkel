package com.eibrahim.winkel.mainPages;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.eibrahim.winkel.declaredClasses.FetchCategory;
import com.eibrahim.winkel.declaredClasses.FetchDataFromFirebase;
import com.eibrahim.winkel.declaredClasses.FetchSpecificData;
import com.eibrahim.winkel.declaredClasses.Search;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {



    private RecyclerView recyclerView_filter, recyclerView_items;
    TextView main_categories, tops_titles;
    private RelativeLayout btnItemsOffers, tops_view;
    private SwipeRefreshLayout fragment_home;
    private FetchDataFromFirebase fetchDataFromFirebase;
    private Boolean filtered = false;
    private String type, fPrice, tPrice;
    private LinearLayout search_page, btns_filters;
    ImageView tops_btn;
    ImageView btnCloseSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView_filter = root.findViewById(R.id.recyclerview_filter);
        recyclerView_items = root.findViewById(R.id.recyclerview_items);
        RecyclerView recyclerview_search = root.findViewById(R.id.recyclerview_search);

        ImageView tops_btn = root.findViewById(R.id.tops_btn);
        PopupMenu popup = new PopupMenu(requireContext(), tops_btn);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        tops_btn.setOnClickListener(v -> popup.show());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.new_releases) {
                doFilter("NewReleases");
                tops_titles.setText("New Releases");
                return true;
            } else if (id == R.id.recommended_item) {
                doFilter("Recommended");
                tops_titles.setText("Recommended");
                return true;
            } else if (id == R.id.trendy) {
                doFilter("Trendy");
                tops_titles.setText("Trendy");
                return true;
            } else if (id == R.id.top_sales_item) {
                doFilter("TopSales");
                tops_titles.setText("Top Sales");
                return true;
            } else if (id == R.id.top_rating_item) {
                doFilter("TopRating");
                tops_titles.setText("Top Rating");
                return true;
            }
            return false;
        });

        LinearLayout btnItemsMens = root.findViewById(R.id.btnItemsMens);
        LinearLayout btnItemsWomen = root.findViewById(R.id.btnItemsWomen);
        LinearLayout btnItemsKids = root.findViewById(R.id.btnItemsKids);

        btns_filters = root.findViewById(R.id.btns_filters);
        btnItemsOffers = root.findViewById(R.id.btnItemsOffers);
        main_categories = root.findViewById(R.id.main_categories);

         tops_view = root.findViewById(R.id.tops_view);
         tops_btn = root.findViewById(R.id.tops_btn);
         tops_titles = root.findViewById(R.id.tops_titles);

        btnCloseSearch = root.findViewById(R.id.btnCloseSearch);

        fragment_home = root.findViewById(R.id.fragment_home);

        LinearLayout search_btn = root.findViewById(R.id.search_btn);
        search_page = root.findViewById(R.id.search_page);

        ImageView btnFilterH = root.findViewById(R.id.btnFunctions);

        functionsBottomSheet functionsBottomSheet = new functionsBottomSheet(this);

        EditText search_text = root.findViewById(R.id.search_text);


        fetchDataFromFirebase = new FetchDataFromFirebase(
                recyclerView_items,
                requireContext()
        );

        fetchData();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        search_btn.setOnClickListener(v -> {

            search_page.setVisibility(View.VISIBLE);
            MainActivity.chipNavigationBar.setVisibility(View.GONE);
            search_text.requestFocus();
            imm.showSoftInput(search_text, InputMethodManager.SHOW_IMPLICIT);
        });

        btnItemsMens.setOnClickListener(v -> doFilter("Mens", "0", "1000"));

        btnItemsWomen.setOnClickListener(v -> doFilter("Womens", "0", "1000"));

        btnItemsKids.setOnClickListener(v -> doFilter("Kids", "0", "1000"));


        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                if (!searchText.isEmpty()) {

                    Search.search(requireContext(), searchText, recyclerview_search);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnCloseSearch.setOnClickListener(v -> {

            search_page.setVisibility(View.GONE);
            //Todo: hide keyboard
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
        doFilter("TopSales");
    }

    public void recyclerviewVisibility(String type){

        switch (type) {

            case "Mens":
            case "Womens":
            case "Kids":
                btnItemsOffers.setVisibility(View.GONE);
                btns_filters.setVisibility(View.GONE);
                main_categories.setVisibility(View.GONE);
                tops_view.setVisibility(View.GONE);
                recyclerView_filter.setVisibility(View.VISIBLE);
                filtered = true;
                break;
            default:
                btnItemsOffers.setVisibility(View.VISIBLE);
                btns_filters.setVisibility(View.VISIBLE);
                main_categories.setVisibility(View.VISIBLE);
                tops_view.setVisibility(View.VISIBLE);
                recyclerView_filter.setVisibility(View.GONE);
                filtered = false;
                break;
        }
    }

    public void doFilter(String type, String fPrice, String tPrice){

        this.type = type;
        this.fPrice = fPrice;
        this.tPrice = tPrice;

        recyclerviewVisibility(type);

        fetchDataFromFirebase.fetchData(type, fPrice, tPrice, recyclerView_items);

        FetchCategory.fetchCategory(type, fPrice, tPrice, recyclerView_filter, recyclerView_items, requireContext());

    }

    public void doFilter(String type){

        recyclerviewVisibility(type);
        FetchSpecificData fetchSpecificData = new FetchSpecificData(recyclerView_items, requireContext());
        fetchSpecificData.fetchIt("Products", type, type);

    }

}