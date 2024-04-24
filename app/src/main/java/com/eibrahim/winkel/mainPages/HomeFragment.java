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
import com.eibrahim.winkel.declaredClasses.RecyclerviewVisibility;
import com.eibrahim.winkel.declaredClasses.Search;

public class HomeFragment extends Fragment {


    private RecyclerView recyclerView_filter;
    private TextView tops_titles;
    private SwipeRefreshLayout fragment_home;
    private LinearLayout search_page;
    protected String type, tPrice, fPrice;
    private DoFilter doFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView_filter = root.findViewById(R.id.recyclerview_filter);
        RecyclerView recyclerView_items = root.findViewById(R.id.recyclerview_items);
        RelativeLayout tops_view = root.findViewById(R.id.tops_view);
        tops_titles = root.findViewById(R.id.tops_titles);
        LinearLayout main_home_design = root.findViewById(R.id.main_home_design);
        fragment_home = root.findViewById(R.id.fragment_home);
        search_page = root.findViewById(R.id.search_page);
        ImageView btnCloseSearch = root.findViewById(R.id.btnCloseSearch);

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

        RecyclerviewVisibility recyclerviewVisibility = new RecyclerviewVisibility(main_home_design, tops_view, recyclerView_filter);

        doFilter = new DoFilter(recyclerView_items, recyclerviewVisibility, requireContext());

        fetchData();
        functionsBottomSheet functionsBottomSheet = new functionsBottomSheet(recyclerView_filter, recyclerView_items, recyclerviewVisibility, doFilter);
        tops_btn.setOnClickListener(v -> popup.show());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.new_releases) {
                doFilter.doFilter("NewReleases");
                tops_titles.setText(getString(R.string.new_releases));
                return true;
            } else if (id == R.id.recommended_item) {
                doFilter.doFilter("Recommended");
                tops_titles.setText(getString(R.string.recommended));
                return true;
            } else if (id == R.id.trendy) {
                doFilter.doFilter("Trendy");
                tops_titles.setText(getString(R.string.trendy));
                return true;
            } else if (id == R.id.top_sales_item) {
                doFilter.doFilter("TopSales");
                tops_titles.setText(getString(R.string.top_sales));
                return true;
            } else if (id == R.id.top_rating_item) {
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
            doFilter.doFilter("Mens", recyclerView_filter);
        });
        btnItemsWomen.setOnClickListener(v -> {
            doFilter.doFilter("Womens", recyclerView_filter);
        });
        btnItemsBoys.setOnClickListener(v -> {
            doFilter.doFilter("Kids", recyclerView_filter);
        });
        btnItemsGirls.setOnClickListener(v -> {
            doFilter.doFilter("Kids", recyclerView_filter);
        });
        btnItemsBabies.setOnClickListener(v -> {
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

            doFilter.lastAction();

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
        doFilter.doFilter("NewReleases");
    }


}