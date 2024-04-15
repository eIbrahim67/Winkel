package com.eibrahim.winkel.secondPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.adapterClasses.adapterRecyclerviewSizes;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
        TextView itemCategoryDetail = findViewById(R.id.itemCategoryDetail);
        TextView itemPriceDetail = findViewById(R.id.itemPriceDetail);
        ImageView btnPlus = findViewById(R.id.btn_plus);
        ImageView btnBackHome = findViewById(R.id.btn_back_home);
        ImageView btnSub = findViewById(R.id.btn_sub);
        TextView itemMuchDetail = findViewById(R.id.itemMuchDetail);
        TextView itemNameDetail = findViewById(R.id.itemNameDetail);
        TextView addToBasketText = findViewById(R.id.addToBasketText);
        ImageView btnBasketD = findViewById(R.id.btn_basketD);
        ImageView addToBasketImg = findViewById(R.id.addToBasketImg);
        LinearLayout addToBasket = findViewById(R.id.addToBasket);
        ImageView btnWishlist = findViewById(R.id.btn_love);
        recyclerview_sizes = findViewById(R.id.recyclerview_sizes);
        TextView reviews_num = findViewById(R.id.reviews_num);

        DocumentReference wishlistRef = firestore.collection("UsersData")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("WishlistCollection")
                .document("wishlistDocument");

        Intent intent = getIntent();
        if (intent != null) {
            currentItem =(DataRecyclerviewMyItem) intent.getSerializableExtra("item");

            Glide.with(this)
                    .load(currentItem.getImageId())
                    .into(itemImgDetail);

            itemCategoryDetail.setText(currentItem.getCategory());
            itemPriceDetail.setText(currentItem.getPrice() + getString(R.string.le));
            itemNameDetail.setText(currentItem.getName());
            reviews_num.setText("244" + getString(R.string.review));
        }

        btnBackHome.setOnClickListener(v -> finish());

        if (currentItem.getItemLoved())
            btnWishlist.setImageResource(R.drawable.loved_icon);
        btnWishlist.setOnClickListener(v -> {

            if (currentItem.getItemLoved()) {
                btnWishlist.setImageResource(R.drawable.unlove_icon_white);
                currentItem.setItemLoved(false);
                wishlistRef
                        .update("WishlistCollection", FieldValue.arrayRemove(currentItem.getItemId() + "," + currentItem.getItemType()))
                        .addOnSuccessListener(unused -> Toast.makeText(ItemDetailActivity.this, R.string.item_removed_from_wishlist_success, Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(ItemDetailActivity.this, R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show());


            } else {
                btnWishlist.setImageResource(R.drawable.loved_icon);
                currentItem.setItemLoved(true);
                wishlistRef
                        .update("WishlistCollection", FieldValue.arrayUnion(currentItem.getItemId()  + "," + currentItem.getItemType()))
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

            String much = (String) itemMuchDetail.getText();
            if(addToBasketText.getVisibility() == View.GONE){
                btnBasketD.callOnClick();
            }else {

                if (adapterRvSizes.getSize().equals("null")){
                    Toast.makeText(ItemDetailActivity.this, getString(R.string.choose_size_prompt), Toast.LENGTH_SHORT).show();
                }
                else {
                    currentItem.setMuch(String.valueOf(itemMuchDetail.getText()));
                    addToBasketText.setVisibility(View.GONE);

                    basketRef
                            .update("BasketCollection", FieldValue.arrayUnion(
                                            currentItem.getItemId() + "," +
                                                    currentItem.getItemType() + "," +
                                                    much + "," +
                                                    adapterRvSizes.getSize()

                                    )
                            )
                            .addOnSuccessListener(unused -> Toast.makeText(ItemDetailActivity.this, getString(R.string.item_added_success), Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(ItemDetailActivity.this, getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show());

                }

            }
        });

        addToBasketImg.setOnClickListener(v -> addToBasket.performClick());


        btnBasketD.setOnClickListener(v -> finish());

        btnPlus.setOnClickListener(v -> itemMuchDetail.setText(String.valueOf(Integer.parseInt((String) itemMuchDetail.getText()) + 1)));

        btnSub.setOnClickListener(v -> itemMuchDetail.setText(String.valueOf(Integer.parseInt((String) itemMuchDetail.getText()) - 1)));

        declareSizes();

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