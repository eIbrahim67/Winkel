package com.eibrahim.winkel.home;

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
import androidx.fragment.app.Fragment;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.FragmentHomeBinding;
import com.eibrahim.winkel.declaredClasses.DoFilter;
import com.eibrahim.winkel.declaredClasses.RecyclerviewVisibility;
import com.eibrahim.winkel.declaredClasses.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;

    private FragmentHomeBinding binding;
    private DoFilter doFilter;
    private functionsBottomSheet functionsBottomSheet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);

        try {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            PopupMenu popup = new PopupMenu(requireContext(), binding.topsBtn);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

            RecyclerviewVisibility recyclerviewVisibility = new RecyclerviewVisibility(
                    binding.mainHomeDesign,
                    binding.topsView,
                    binding.recyclerviewFilter
            );

            doFilter = new DoFilter(binding.recyclerviewItems, recyclerviewVisibility, requireContext());

            fetchData();

            functionsBottomSheet = new functionsBottomSheet(
                    binding.recyclerviewFilter,
                    binding.recyclerviewItems,
                    recyclerviewVisibility,
                    doFilter
            );

            // Top Menu Popup Click
            binding.topsBtn.setOnClickListener(v -> popup.show());

            popup.setOnMenuItemClickListener(item -> {
                try {
                    int id = item.getItemId();
                    if (id == R.id.new_releases) {
                        doFilter.doFilter("NewReleases", binding.skeletonLayout);
                        binding.topsTitles.setText(getString(R.string.new_releases));
                    } else if (id == R.id.recommended_item) {
                        doFilter.doFilter("Recommended", binding.skeletonLayout);
                        binding.topsTitles.setText(getString(R.string.recommended));
                    } else if (id == R.id.trendy) {
                        doFilter.doFilter("Trendy", binding.skeletonLayout);
                        binding.topsTitles.setText(getString(R.string.trendy));
                    } else if (id == R.id.top_sales_item) {
                        doFilter.doFilter("TopSales", binding.skeletonLayout);
                        binding.topsTitles.setText(getString(R.string.top_sales));
                    } else if (id == R.id.top_rating_item) {
                        doFilter.doFilter("TopRating", binding.skeletonLayout);
                        binding.topsTitles.setText(getString(R.string.top_rating));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            });

            binding.searchBtn.setOnClickListener(v -> {
                binding.searchPage.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.GONE);
                binding.searchText.requestFocus();
                imm.showSoftInput(binding.searchText, InputMethodManager.SHOW_IMPLICIT);
            });

            setupCategoryButtons();

            binding.searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String searchText = s.toString().trim();
                        if (!searchText.isEmpty()) {
                            Search.search(requireContext(), searchText, binding.recyclerviewSearch);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            binding.btnCloseSearch.setOnClickListener(v -> {
                binding.searchPage.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(binding.searchText.getWindowToken(), 0);
            });

            binding.btnFunctions.setOnClickListener(v -> {
                if (functionsBottomSheet.isVisible()) {
                    functionsBottomSheet.dismiss();
                } else {
                    functionsBottomSheet.show(requireActivity().getSupportFragmentManager(), "");
                }
            });

            binding.fragmentHome.setOnRefreshListener(() -> {
                try {
                    doFilter.lastAction();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    binding.fragmentHome.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return binding.getRoot();
    }

    private void setupCategoryButtons() {
        binding.btnItemsMens.setOnClickListener(v -> doFilter.doFilter("Mens", binding.recyclerviewFilter, binding.skeletonLayout));
        binding.btnItemsWomen.setOnClickListener(v -> doFilter.doFilter("Womens", binding.recyclerviewFilter, binding.skeletonLayout));
        binding.btnItemsBoys.setOnClickListener(v -> doFilter.doFilter("Kids", binding.recyclerviewFilter, binding.skeletonLayout));
        binding.btnItemsGirls.setOnClickListener(v -> doFilter.doFilter("Kids", binding.recyclerviewFilter, binding.skeletonLayout));
        binding.btnItemsBabies.setOnClickListener(v -> doFilter.doFilter("Kids", binding.recyclerviewFilter, binding.skeletonLayout));
    }

    private void fetchData() {
        try {
            if (binding.skeletonLayout != null) {
                doFilter.doFilter("NewReleases", binding.skeletonLayout);
            } else {
                doFilter.doFilter("NewReleases");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (binding.searchPage.getVisibility() == View.VISIBLE) {
                        binding.searchPage.setVisibility(View.GONE);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    } else if (binding.mainHomeDesign.getVisibility() == View.GONE) {
                        doFilter.doFilter("NewReleases");
                    } else {
                        requireActivity().moveTaskToBack(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // prevent memory leaks
    }
}
