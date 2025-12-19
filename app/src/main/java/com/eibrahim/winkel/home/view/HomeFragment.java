package com.eibrahim.winkel.home.view;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.core.DoFilter;
import com.eibrahim.winkel.core.RecyclerviewVisibility;
import com.eibrahim.winkel.core.adapterRecyclerviewItems;
import com.eibrahim.winkel.databinding.FragmentHomeBinding;
import com.eibrahim.winkel.home.bottomSheet.functionsBottomSheet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BottomNavigationView bottomNavigationView;

    private DoFilter doFilter;
    private functionsBottomSheet functionsBottomSheet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        InputMethodManager imm =
                (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        PopupMenu popup = new PopupMenu(requireContext(), binding.topsBtn);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        RecyclerviewVisibility recyclerviewVisibility =
                new RecyclerviewVisibility(
                        binding.mainHomeDesign,
                        binding.topsView,
                        binding.recyclerviewFilter
                );

        doFilter = new DoFilter(
                binding.recyclerviewItems,
                recyclerviewVisibility,
                requireContext()
        );

        functionsBottomSheet =
                new functionsBottomSheet(
                        binding.recyclerviewFilter,
                        binding.recyclerviewItems,
                        recyclerviewVisibility,
                        doFilter, binding.skeletonLayout
                );

        loadHomeDefault();
        setupTopMenu(popup);
        setupSearch(imm);
        setupCategoryButtons();
        setupSwipeRefresh();
        setupFunctionsSheet();

        return binding.getRoot();
    }

    /* -------------------- Setup Methods -------------------- */

    private void loadHomeDefault() {
        doFilter.doFilter("NewReleases", binding.skeletonLayout);
        binding.topsTitles.setText(getString(R.string.new_releases));
    }

    private void setupTopMenu(PopupMenu popup) {

        binding.topsBtn.setOnClickListener(v -> popup.show());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.new_releases) {
                doFilter.doFilter("NewReleases", binding.skeletonLayout);
                binding.topsTitles.setText(getString(R.string.new_releases));
            }
            else if (item.getItemId() == R.id.recommended_item) {
                doFilter.doFilter("Recommended", binding.skeletonLayout);
                binding.topsTitles.setText(getString(R.string.recommended));
            }
            else if (item.getItemId() == R.id.trendy) {
                doFilter.doFilter("Trendy", binding.skeletonLayout);
                binding.topsTitles.setText(getString(R.string.trendy));
            }
            else if (item.getItemId() == R.id.top_sales_item) {
                doFilter.doFilter("TopSales", binding.skeletonLayout);
                binding.topsTitles.setText(getString(R.string.top_sales));
            }
            else if (item.getItemId() == R.id.top_rating_item) {
                doFilter.doFilter("TopRating", binding.skeletonLayout);
                binding.topsTitles.setText(getString(R.string.top_rating));
            }
            return false;
        });
    }

    private void setupSearch(InputMethodManager imm) {

        binding.searchBtn.setOnClickListener(v -> {
            binding.searchPage.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.GONE);
            binding.searchText.requestFocus();
            imm.showSoftInput(binding.searchText, InputMethodManager.SHOW_IMPLICIT);
        });

        binding.btnCloseSearch.setOnClickListener(v -> {
            binding.searchPage.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            imm.hideSoftInputFromWindow(binding.searchText.getWindowToken(), 0);
        });

        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    search(requireContext(), query, binding.recyclerviewSearch);
                }
            }
        });
    }

    private void setupCategoryButtons() {
        binding.btnItemsMens.setOnClickListener(v ->
                doFilter.doFilter("Mens", binding.recyclerviewFilter, binding.skeletonLayout));

        binding.btnItemsWomen.setOnClickListener(v ->
                doFilter.doFilter("Womens", binding.recyclerviewFilter, binding.skeletonLayout));

        binding.btnItemsBoys.setOnClickListener(v ->
                doFilter.doFilter("Kids", binding.recyclerviewFilter, binding.skeletonLayout));

        binding.btnItemsGirls.setOnClickListener(v ->
                doFilter.doFilter("Kids", binding.recyclerviewFilter, binding.skeletonLayout));

        binding.btnItemsBabies.setOnClickListener(v ->
                doFilter.doFilter("Kids", binding.recyclerviewFilter, binding.skeletonLayout));
    }

    private void setupSwipeRefresh() {
        binding.fragmentHome.setOnRefreshListener(() -> {
            doFilter.lastAction();
            binding.fragmentHome.setRefreshing(false);
        });
    }

    private void setupFunctionsSheet() {
        binding.btnFunctions.setOnClickListener(v -> {
            if (functionsBottomSheet.isVisible()) {
                functionsBottomSheet.dismiss();
            } else {
                functionsBottomSheet.show(
                        requireActivity().getSupportFragmentManager(),
                        "functions_sheet"
                );
            }
        });
    }

    /* -------------------- Back Handling -------------------- */

    @Override
    public void onResume() {
        super.onResume();

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (binding.searchPage.getVisibility() == View.VISIBLE) {
                            binding.searchPage.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        } else if (binding.mainHomeDesign.getVisibility() == View.GONE) {
                            doFilter.doFilter("NewReleases", binding.skeletonLayout);
                        } else {
                            requireActivity().moveTaskToBack(true);
                        }
                    }
                });
    }

    /* -------------------- Search -------------------- */

    public static void search(Context context, String searchText, RecyclerView recyclerView) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef =
                firestore.collection("Products")
                        .document("Mens")
                        .collection("Mens");

        collectionRef.get().addOnSuccessListener(querySnapshot -> {

            List<DataRecyclerviewMyItem> results = new ArrayList<>();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> data = document.getData();
                if (data == null) continue;

                String name = (String) data.get("name");

                if (name != null && name.toLowerCase().contains(searchText.toLowerCase())) {
                    DataRecyclerviewMyItem item =
                            new DataRecyclerviewMyItem(
                                    (String) data.get("category"),
                                    (String) data.get("imageId"),
                                    name,
                                    (String) data.get("price"),
                                    "Mens",
                                    ""
                            );

                    item.setItemId((String) data.get("itemId"));
                    item.setItemLoved(false);
                    results.add(item);
                }
            }

            recyclerView.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            recyclerView.setAdapter(
                    new adapterRecyclerviewItems(context, results));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
