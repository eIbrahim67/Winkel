package com.eibrahim.winkel.secondPages.item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.AdapterRecyclerviewReviews;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewSizes;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.eibrahim.winkel.dataClasses.DataReviewItem;
import com.eibrahim.winkel.dialogs.AddedToBasketDialog;
import com.eibrahim.winkel.mainPages.MainActivity;
import com.eibrahim.winkel.publicDataSender.publicData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemDetailActivity extends AppCompatActivity {

    private DataRecyclerviewMyItem currentItem;
    private adapterRecyclerviewSizes adapterRvSizes;
    private RecyclerView recyclerview_sizes;
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        ImageView itemImgDetail = findViewById(R.id.itemImgDetail);
        TextView itemPriceDetail = findViewById(R.id.itemPriceDetail);
        ImageView btnBackHome = findViewById(R.id.btn_back_home);
        TextView itemNameDetail = findViewById(R.id.itemNameDetail);
        TextView addToBasketText = findViewById(R.id.addToBasketText);
        ImageView btnBasketD = findViewById(R.id.btn_basketD);
        LinearLayout addToBasket = findViewById(R.id.addToBasket);
        ImageView btnWishlist = findViewById(R.id.btn_love);
        recyclerview_sizes = findViewById(R.id.recyclerview_sizes);
        RecyclerView recyclerview_reviews = findViewById(R.id.recyclerview_reviews);
        TextView item_description = findViewById(R.id.item_description);
        LinearLayout description_btn = findViewById(R.id.description_btn);
        LinearLayout review_btn = findViewById(R.id.review_btn);
        ImageView down_arrow_description = findViewById(R.id.down_arrow_description);
        ImageView down_arrow_reviews = findViewById(R.id.down_arrow_reviews);
        ImageView up_arrow_description = findViewById(R.id.up_arrow_description);
        ImageView up_arrow_reviews = findViewById(R.id.up_arrow_reviews);

        description_btn.setOnClickListener(v -> {
            v = item_description;
            if (v.getVisibility() == View.GONE) {
                v.setVisibility(View.VISIBLE);
                down_arrow_description.setVisibility(View.INVISIBLE);
                up_arrow_description.setVisibility(View.VISIBLE);
            }
            else {
                v.setVisibility(View.GONE);
                down_arrow_description.setVisibility(View.VISIBLE);
                up_arrow_description.setVisibility(View.INVISIBLE);

            }

        });

        review_btn.setOnClickListener(v -> {
            v = recyclerview_reviews;
            if (v.getVisibility() == View.GONE) {
                v.setVisibility(View.VISIBLE);
                down_arrow_reviews.setVisibility(View.INVISIBLE);
                up_arrow_reviews.setVisibility(View.VISIBLE);
            }
            else {
                v.setVisibility(View.GONE);
                down_arrow_reviews.setVisibility(View.VISIBLE);
                up_arrow_reviews.setVisibility(View.INVISIBLE);

            }

        });


        AddedToBasketDialog addedToBasketDialog = new AddedToBasketDialog();

        DocumentReference wishlistRef = firestore.collection("UsersData")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("Wishlist")
                .document("Wishlist");

        Intent intent = getIntent();
        if (intent != null) {
            currentItem =(DataRecyclerviewMyItem) intent.getSerializableExtra("item");

            Glide.with(this)
                    .load(currentItem.getImageId())
                    .into(itemImgDetail);

            String temp = currentItem.getPrice() + getString(R.string.le);

            itemPriceDetail.setText(temp);
            itemNameDetail.setText(currentItem.getName());
        }

        btnBackHome.setOnClickListener(v -> finish());

        if (currentItem.getItemLoved())
            btnWishlist.setImageResource(R.drawable.loved_icon);
        btnWishlist.setOnClickListener(v -> {

            if (currentItem.getItemLoved()) {
                btnWishlist.setImageResource(R.drawable.unlove_icon_black);
                currentItem.setItemLoved(false);
                wishlistRef
                        .update("Wishlist", FieldValue.arrayRemove(currentItem.getItemId() + "," + currentItem.getItemType()))
                        .addOnSuccessListener(unused -> Toast.makeText(ItemDetailActivity.this, R.string.item_removed_from_wishlist_success, Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ItemDetailActivity.this, R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show());


            } else {
                btnWishlist.setImageResource(R.drawable.loved_icon);
                currentItem.setItemLoved(true);
                wishlistRef
                        .update("Wishlist", FieldValue.arrayUnion(currentItem.getItemId()  + "," + currentItem.getItemType()))
                        .addOnSuccessListener(unused -> Toast.makeText(ItemDetailActivity.this, R.string.item_added_to_wishlist_success, Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ItemDetailActivity.this, R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show());


            }

        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        DocumentReference basketRef = firestore.collection("UsersData")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("BasketCollection")
                .document("BasketDocument");

        addToBasket.setOnClickListener(v -> {

            if(addToBasketText.getVisibility() == View.GONE){
                btnBasketD.callOnClick();
            }else {

                if (adapterRvSizes.getSize().equals("null")){
                    Toast.makeText(ItemDetailActivity.this, getString(R.string.choose_size_prompt), Toast.LENGTH_SHORT).show();
                }
                else {
                    currentItem.setMuch("1");

                    basketRef
                            .update("BasketCollection", FieldValue.arrayUnion(
                                            currentItem.getItemId() + "," +
                                                    currentItem.getItemType() + "," +
                                                    currentItem.getMuch() + "," +
                                                    adapterRvSizes.getSize()

                                    )
                            )
                            .addOnSuccessListener( a -> {

                                addedToBasketDialog.show(getSupportFragmentManager(), "");

                            })
                            .addOnFailureListener(e -> Toast.makeText(ItemDetailActivity.this, getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show());

                }

            }
        });

        btnBasketD.setOnClickListener(v -> {
            publicData.basketClicked = true;
            finish();
        });

        declareSizes();

        List<DataReviewItem> itemList = new ArrayList<>();
        String logoUrl = "https://firebasestorage.googleapis.com/v0/b/winkel-eibrahim.appspot.com/o/images%20of%20vendors%2FRa'd.png?alt=media&token=e820f867-1378-4c19-9f13-ee472150ca8d";
        String name = "Winkel";
        String username = "@winkel";

        itemList.add(new DataReviewItem(logoUrl, name, username, "Great product!", "5"));
        itemList.add(new DataReviewItem(logoUrl, name, username, "Excellent service!", "4"));
        itemList.add(new DataReviewItem(logoUrl, name, username, "Fast delivery!", "4.5"));
        itemList.add(new DataReviewItem(logoUrl, name, username, "Amazing quality!", "4.8"));
        itemList.add(new DataReviewItem(logoUrl, name, username, "Responsive customer support!", "4.7"));
        itemList.add(new DataReviewItem(logoUrl, name, username, "Impressive packaging!", "4.6"));
        itemList.add(new DataReviewItem(logoUrl, name, username, "Bad product", "1.0"));

        AdapterRecyclerviewReviews adapter = new AdapterRecyclerviewReviews(this, itemList);
        recyclerview_reviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerview_reviews.setAdapter(adapter);

    }

    private void declareSizes(){

        List<String> dataOfRvFilter = new ArrayList<>();
        dataOfRvFilter.add("S");
        dataOfRvFilter.add("M");
        dataOfRvFilter.add("L");
        dataOfRvFilter.add("XL");
        dataOfRvFilter.add("Special Size");

        adapterRvSizes = new adapterRecyclerviewSizes(dataOfRvFilter);
        recyclerview_sizes.setLayoutManager(new LinearLayoutManager(ItemDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerview_sizes.setAdapter(adapterRvSizes);
    }

}