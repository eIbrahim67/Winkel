package com.eibrahim.winkel.item;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        setupDescriptionToggle();
        setupReviewToggle();
        declareSizes();
        loadReviews();

        AddedToBasketDialog dialog = new AddedToBasketDialog();

        binding.addToBasket.setOnClickListener(v -> {

            // 1️⃣ Check size selection
            if (adapterRvSizes == null || "null".equals(adapterRvSizes.getSize())) {
                Snackbar.make(binding.getRoot(), getString(R.string.choose_size_prompt), Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (currentItem == null) return;

            // 2️⃣ Lock UI
            binding.addToBasket.setEnabled(false);
            binding.addToBasket.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);

            String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
            String itemId = currentItem.getItemId();
            String size = adapterRvSizes.getSize().trim();

            // 3️⃣ Composite document ID: itemId + size
            String docId = itemId + "_" + size;
            DocumentReference itemRef = firestore.collection("UsersData")
                    .document(uid)
                    .collection("Basket")
                    .document(docId);

            // 4️⃣ Transaction: increment if exists, create if not
            firestore.runTransaction(transaction -> {

                DocumentSnapshot snap = transaction.get(itemRef);

                if (snap.exists()) {
                    // Increment quantity
                    transaction.update(itemRef, "quantity", FieldValue.increment(1));
                } else {
                    double price = 0.0;
                    try {
                        price = Double.parseDouble(currentItem.getPrice());
                    } catch (NumberFormatException ignored) {}

                    Map<String, Object> data = new HashMap<>();
                    data.put("itemId", itemId);
                    data.put("itemType", currentItem.getItemType());
                    data.put("size", size);
                    data.put("quantity", 1L);
                    data.put("price", price);
                    data.put("addedAt", FieldValue.serverTimestamp());

                    transaction.set(itemRef, data);
                }

                return null;

            }).addOnSuccessListener(a -> {
                // 5️⃣ Unlock UI
                binding.addToBasket.setEnabled(true);
                binding.addToBasket.setText(R.string.add_to_basket);
                binding.progressBar.setVisibility(View.GONE);

                // 6️⃣ Show success dialog
                dialog.show(getSupportFragmentManager(), "");
            }).addOnFailureListener(e -> {
                // 7️⃣ Unlock UI on error
                binding.addToBasket.setEnabled(true);
                binding.addToBasket.setText(R.string.add_to_basket);
                binding.progressBar.setVisibility(View.GONE);

                Snackbar.make(binding.getRoot(), R.string.unexpected_error_occurred, Snackbar.LENGTH_SHORT).show();
            });
        });

        // Optional: navigate to basket
        binding.btnBasketD.setOnClickListener(v -> {
            publicData.basketClicked = true;
            finish();
        });

        binding.btnBack.setOnClickListener(v -> finish());
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
                wishlistRef.update("Wishlist", FieldValue.arrayRemove(currentItem.getItemId() + "," + currentItem.getItemType())).addOnSuccessListener(unused -> Snackbar.make(binding.getRoot(), R.string.item_removed_from_wishlist_success, Snackbar.LENGTH_SHORT).show()).addOnFailureListener(e -> Snackbar.make(binding.getRoot(), R.string.unexpected_error_occurred, Snackbar.LENGTH_SHORT).show());
            } else {
                currentItem.setItemLoved(true);
                binding.btnLove.setImageResource(R.drawable.loved_icon);
                wishlistRef.update("Wishlist", FieldValue.arrayUnion(currentItem.getItemId() + "," + currentItem.getItemType())).addOnSuccessListener(unused -> Snackbar.make(binding.getRoot(), R.string.item_added_to_wishlist_success, Snackbar.LENGTH_SHORT).show()).addOnFailureListener(e -> Snackbar.make(binding.getRoot(), R.string.unexpected_error_occurred, Snackbar.LENGTH_SHORT).show());
            }
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
        sizes.add("XXL");
        sizes.add("XXXL");

        adapterRvSizes = new adapterRecyclerviewSizes(sizes);
        binding.recyclerviewSizes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerviewSizes.setAdapter(adapterRvSizes);
    }

    private final List<DataReviewItem> reviewsList = new ArrayList<>();
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
            AdapterRecyclerviewReviews adapter = new AdapterRecyclerviewReviews(reviewsList);
            binding.recyclerviewReviews.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerviewReviews.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            reviewsLoaded = true;
            reviewsEmpty = true;
        });
    }

}
