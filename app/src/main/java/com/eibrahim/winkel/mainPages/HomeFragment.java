package com.eibrahim.winkel.mainPages;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.bottomSheets.functionsBottomSheet;
import com.eibrahim.winkel.declaredClasses.DoFilter;
import com.eibrahim.winkel.declaredClasses.FetchCategory;
import com.eibrahim.winkel.declaredClasses.FetchDataFromFirebase;
import com.eibrahim.winkel.declaredClasses.Search;

public class HomeFragment extends Fragment {



    private RecyclerView recyclerView_filter, recyclerView_items;
    TextView main_categories, tops_titles;
    private RelativeLayout btnItemsOffers, tops_view;
    private SwipeRefreshLayout fragment_home;
    private Boolean filtered = false;
    private String type, fPrice, tPrice;
    private LinearLayout search_page, btns_filters;
    ImageView btnCloseSearch;
    private DoFilter doFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView_filter = root.findViewById(R.id.recyclerview_filter);
        recyclerView_items = root.findViewById(R.id.recyclerview_items);
        tops_view = root.findViewById(R.id.tops_view);
        tops_titles = root.findViewById(R.id.tops_titles);
        btns_filters = root.findViewById(R.id.btns_filters);
        btnItemsOffers = root.findViewById(R.id.btnItemsOffers);
        main_categories = root.findViewById(R.id.main_categories);
        fragment_home = root.findViewById(R.id.fragment_home);
        search_page = root.findViewById(R.id.search_page);
        btnCloseSearch = root.findViewById(R.id.btnCloseSearch);

        RecyclerView recyclerview_search = root.findViewById(R.id.recyclerview_search);
        ImageView tops_btn = root.findViewById(R.id.tops_btn);
        LinearLayout search_btn = root.findViewById(R.id.search_btn);
        LinearLayout btnItemsMens = root.findViewById(R.id.btnItemsMens);
        LinearLayout btnItemsWomen = root.findViewById(R.id.btnItemsWomen);
        LinearLayout btnItemsBoys = root.findViewById(R.id.btnItemsBoys);
        LinearLayout btnItemsGirls = root.findViewById(R.id.btnItemsGirls);
        LinearLayout btnItemsBabies = root.findViewById(R.id.btnItemsBabies);
        ImageView btnFilterH = root.findViewById(R.id.btnFunctions);
        EditText search_text = root.findViewById(R.id.search_text);


        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        PopupMenu popup = new PopupMenu(requireContext(), tops_btn);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        doFilter = new DoFilter(recyclerView_items, requireContext());

        fetchData();

        functionsBottomSheet functionsBottomSheet = new functionsBottomSheet(recyclerView_filter, recyclerView_items);
        tops_btn.setOnClickListener(v -> popup.show());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.new_releases) {
                recyclerviewVisibility(type);
                doFilter.doFilter("NewReleases");
                tops_titles.setText(getString(R.string.new_releases));
                return true;
            } else if (id == R.id.recommended_item) {
                recyclerviewVisibility(type);
                doFilter.doFilter("Recommended");
                tops_titles.setText(getString(R.string.recommended));
                return true;
            } else if (id == R.id.trendy) {
                doFilter.doFilter("Trendy");
                recyclerviewVisibility(type);
                tops_titles.setText(getString(R.string.trendy));
                return true;
            } else if (id == R.id.top_sales_item) {
                recyclerviewVisibility(type);
                doFilter.doFilter("TopSales");
                tops_titles.setText(getString(R.string.top_sales));
                return true;
            } else if (id == R.id.top_rating_item) {
                recyclerviewVisibility(type);
                doFilter.doFilter("TopRating");
                tops_titles.setText(getString(R.string.top_rating));
                return true;
            }
            return false;
        });
        search_btn.setOnClickListener(v -> {

            search_page.setVisibility(View.VISIBLE);
            MainActivity.chipNavigationBar.setVisibility(View.GONE);
            search_text.requestFocus();
            imm.showSoftInput(search_text, InputMethodManager.SHOW_IMPLICIT);
        });
        btnItemsMens.setOnClickListener(v -> {
            recyclerviewVisibility("Mens");
            doFilter.doFilter("Mens", recyclerView_filter);
        });
        btnItemsWomen.setOnClickListener(v -> {
            recyclerviewVisibility("Womens");
            doFilter.doFilter("Womens", recyclerView_filter);
        });
        btnItemsBoys.setOnClickListener(v -> {
            recyclerviewVisibility("Kids");
            doFilter.doFilter("Kids", recyclerView_filter);
        });
        btnItemsGirls.setOnClickListener(v -> {
            recyclerviewVisibility("Kids");
            doFilter.doFilter("Kids", recyclerView_filter);
        });
        btnItemsBabies.setOnClickListener(v -> {
            recyclerviewVisibility("Kids");
            doFilter.doFilter("Kids", recyclerView_filter);
        });
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

            if (filtered){
                recyclerviewVisibility(type);
                doFilter.doFilter(type, fPrice, tPrice, recyclerView_filter);
            }
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
        recyclerviewVisibility("NewReleases");
        doFilter.doFilter("NewReleases");
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

}