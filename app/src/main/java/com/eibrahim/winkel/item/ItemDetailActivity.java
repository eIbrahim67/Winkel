package com.eibrahim.winkel.item;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.core.DataReviewItem;
import com.eibrahim.winkel.databinding.ActivityItemDetailBinding;
import com.eibrahim.winkel.item.dialogs.AddedToBasketDialog;
import com.eibrahim.winkel.main.LocaleHelper;
import com.eibrahim.winkel.publicDataSender.publicData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemDetailActivity extends AppCompatActivity {

    private ActivityItemDetailBinding binding;
    private DataRecyclerviewMyItem currentItem;
    private adapterRecyclerviewSizes adapterRvSizes;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");
        Context context = LocaleHelper.setLocale(newBase, language);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applyTheme();
        super.onCreate(savedInstanceState);

        binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupIntentData();
        setupWishlistButton();
        setupAddToBasket();
        setupDescriptionToggle();
        setupReviewToggle();
        declareSizes();
        loadReviews();
        binding.btnBackHome.setOnClickListener(v -> finish());
    }

    private void applyTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        int isDarkMode = sharedPreferences.getInt("theme_state", -1);
        if (isDarkMode != -1) {
            AppCompatDelegate.setDefaultNightMode(isDarkMode == 1 ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setupIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            currentItem = (DataRecyclerviewMyItem) intent.getSerializableExtra("item");
            if (currentItem != null) {
                Glide.with(this).load(currentItem.getImageId()).into(binding.itemImgDetail);
                binding.itemPriceDetail.setText(currentItem.getPrice() + getString(R.string.le));
                binding.itemNameDetail.setText(currentItem.getName());
                binding.btnLove.setImageResource(currentItem.getItemLoved() ? R.drawable.loved_icon : R.drawable.unlove_icon_black);
            }
        }
    }

    private void setupWishlistButton() {
        DocumentReference wishlistRef = firestore.collection("UsersData").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).collection("Wishlist").document("Wishlist");

        binding.btnLove.setOnClickListener(v -> {
            if (currentItem == null) return;

            if (currentItem.getItemLoved()) {
                currentItem.setItemLoved(false);
                binding.btnLove.setImageResource(R.drawable.unlove_icon_black);
                wishlistRef.update("Wishlist", FieldValue.arrayRemove(currentItem.getItemId() + "," + currentItem.getItemType())).addOnSuccessListener(unused -> Toast.makeText(this, R.string.item_removed_from_wishlist_success, Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(this, R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show());
            } else {
                currentItem.setItemLoved(true);
                binding.btnLove.setImageResource(R.drawable.loved_icon);
                wishlistRef.update("Wishlist", FieldValue.arrayUnion(currentItem.getItemId() + "," + currentItem.getItemType())).addOnSuccessListener(unused -> Toast.makeText(this, R.string.item_added_to_wishlist_success, Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(this, R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupAddToBasket() {
        AddedToBasketDialog addedToBasketDialog = new AddedToBasketDialog();

        DocumentReference basketRef = firestore.collection("UsersData").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).collection("BasketCollection").document("BasketDocument");

        binding.addToBasket.setOnClickListener(v -> {
            if (adapterRvSizes == null || "null".equals(adapterRvSizes.getSize())) {
                Toast.makeText(this, getString(R.string.choose_size_prompt), Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentItem != null) {
                currentItem.setMuch("1");
                basketRef.update("BasketCollection", FieldValue.arrayUnion(currentItem.getItemId() + "," + currentItem.getItemType() + "," + currentItem.getMuch() + "," + adapterRvSizes.getSize())).addOnSuccessListener(a -> addedToBasketDialog.show(getSupportFragmentManager(), "")).addOnFailureListener(e -> Toast.makeText(this, getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show());
            }
        });

        binding.btnBasketD.setOnClickListener(v -> {
            publicData.basketClicked = true;
            finish();
        });
    }

    private void setupDescriptionToggle() {
        binding.descriptionBtn.setOnClickListener(v -> {
            if (binding.itemDescription.getVisibility() == View.GONE) {
                binding.itemDescription.setVisibility(View.VISIBLE);
                binding.downArrowDescription.setVisibility(View.INVISIBLE);
                binding.upArrowDescription.setVisibility(View.VISIBLE);
            } else {
                binding.itemDescription.setVisibility(View.GONE);
                binding.downArrowDescription.setVisibility(View.VISIBLE);
                binding.upArrowDescription.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setupReviewToggle() {
        binding.reviewBtn.setOnClickListener(v -> {
            if (binding.recyclerviewReviews.getVisibility() == View.GONE && binding.msgEmptyReviews.getVisibility() == View.GONE) {

                // Show the RecyclerView or empty message
                if (!reviewsLoaded) {
                    loadReviews(); // optional: reload if not loaded yet
                }

                if (reviewsEmpty) {
                    binding.msgEmptyReviews.setVisibility(View.VISIBLE);
                    binding.recyclerviewReviews.setVisibility(View.GONE);
                } else {
                    binding.msgEmptyReviews.setVisibility(View.GONE);
                    binding.recyclerviewReviews.setVisibility(View.VISIBLE);
                }

                binding.downArrowReviews.setVisibility(View.GONE);
                binding.upArrowReviews.setVisibility(View.VISIBLE);

            } else {
                binding.recyclerviewReviews.setVisibility(View.GONE);
                binding.msgEmptyReviews.setVisibility(View.GONE);
                binding.downArrowReviews.setVisibility(View.VISIBLE);
                binding.upArrowReviews.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void declareSizes() {
        List<String> sizes = new ArrayList<>();
        sizes.add("S");
        sizes.add("M");
        sizes.add("L");
        sizes.add("XL");
        sizes.add(getString(R.string.special_size));

        adapterRvSizes = new adapterRecyclerviewSizes(sizes);
        binding.recyclerviewSizes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerviewSizes.setAdapter(adapterRvSizes);
    }

    private List<DataReviewItem> reviewsList = new ArrayList<>();
    private boolean reviewsLoaded = false;
    private boolean reviewsEmpty = false;

    private void loadReviews() {
        if (currentItem == null) return;

        firestore.collection("Items").document(currentItem.getItemId()).collection("Reviews").get().addOnSuccessListener(snap -> {
            reviewsList.clear();
            for (var doc : snap) {
                DataReviewItem item = doc.toObject(DataReviewItem.class);
                reviewsList.add(item);
            }

            reviewsLoaded = true;
            reviewsEmpty = reviewsList.isEmpty();

            // Prepare adapter but don't show yet
            AdapterRecyclerviewReviews adapter = new AdapterRecyclerviewReviews(this, reviewsList);
            binding.recyclerviewReviews.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerviewReviews.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            reviewsLoaded = true;
            reviewsEmpty = true;
        });
    }

}
