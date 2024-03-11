package com.eibrahim.winkel.secondActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.mianActivity.MainActivity;
import com.eibrahim.winkel.mianActivity.WishlistFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.nio.file.FileStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemDetailActivity extends AppCompatActivity {

    private DataRecyclerviewItem currentItem;

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
        TextView addtobasketText = findViewById(R.id.addtobasketText);
        ImageView btnBasketD = findViewById(R.id.btn_basketD);
        ImageView addtobasketImg = findViewById(R.id.addtobasketImg);
        LinearLayout addtobasket = findViewById(R.id.addtobasket);
        ImageView btnWishlist = findViewById(R.id.btn_love);


        Intent intent = getIntent();
        if (intent != null) {
            currentItem =(DataRecyclerviewItem) intent.getSerializableExtra("item");

            Picasso.with(this).load(currentItem.getImageId()).into(itemImgDetail);
            itemCategoryDetail.setText(currentItem.getCategory());
            itemPriceDetail.setText("$" + currentItem.getPrice());
            itemNameDetail.setText(currentItem.getName());

        }

        btnBackHome.setOnClickListener(v -> finish());

        btnWishlist.setOnClickListener(v -> {

        });
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        DocumentReference basketRef = firestore.collection("UsersData")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("BasketCollection")
                .document("BasketDocument");

        addtobasket.setOnClickListener(v -> {

            String much = (String) itemMuchDetail.getText();
            if(addtobasketText.getVisibility() == View.GONE){
                btnBasketD.callOnClick();
            }else {
                currentItem.setMuch(String.valueOf(itemMuchDetail.getText()));
                addtobasketText.setVisibility(View.GONE);

                basketRef
                        .update("BasketCollection", FieldValue.arrayUnion(
                                currentItem.getItemId() + "," +
                                        currentItem.getItemType() + "," +
                                        much
                                )
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ItemDetailActivity.this, "Item added into your Basket", Toast.LENGTH_SHORT).show();
                                WishlistFragment.wishlistIds.add(currentItem.getItemId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ItemDetailActivity.this, "unExpected error", Toast.LENGTH_SHORT).show();
                            }
                        });

                Toast.makeText(ItemDetailActivity.this, "Item added into your Basket", Toast.LENGTH_SHORT).show();
            }
        });

        addtobasketImg.setOnClickListener(v -> addtobasket.performClick());


        btnBasketD.setOnClickListener(v -> {
            Intent intentHomeActivity = new Intent(ItemDetailActivity.this, MainActivity.class);
            intentHomeActivity.putExtra("state", 1);
            startActivity(intentHomeActivity);
            finish();
        });

        btnPlus.setOnClickListener(v -> itemMuchDetail.setText(String.valueOf(Integer.parseInt((String) itemMuchDetail.getText()) + 1)));

        btnSub.setOnClickListener(v -> itemMuchDetail.setText(String.valueOf(Integer.parseInt((String) itemMuchDetail.getText()) - 1)));

    }

}