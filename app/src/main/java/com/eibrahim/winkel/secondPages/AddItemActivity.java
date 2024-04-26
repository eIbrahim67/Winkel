package com.eibrahim.winkel.secondPages;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.declaredClasses.AddToShop;
import com.google.android.material.imageview.ShapeableImageView;

import android.net.Uri;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddItemActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private ShapeableImageView loading_image;
    private EditText item_category;
    private EditText item_title;
    private EditText item_price;
    private RadioButton radioButtonForMen, radioButtonForKids;
    String title, price, category;
    String TypeFor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);

        loading_image = findViewById(R.id.loading_image);
        item_category = findViewById(R.id.post_details);
        item_title = findViewById(R.id.post_title);
        item_price = findViewById(R.id.post_price);

        radioButtonForMen = findViewById(R.id.for_men);
        radioButtonForKids = findViewById(R.id.for_kids);

        TextView upload_btn = findViewById(R.id.upload_btn);
        RelativeLayout upload_photo = findViewById(R.id.upload_photo);
        RelativeLayout take_photo = findViewById(R.id.take_photo);

        AddToShop addToShop = new AddToShop();

        take_photo.setOnClickListener(view -> dispatchTakePictureIntent());

        upload_photo.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        });

        upload_btn.setOnClickListener(view -> {

            price = item_price.getText().toString();
            category = item_category.getText().toString();
            title = item_title.getText().toString();

            if (radioButtonForMen.isChecked())
                TypeFor = "Mens";
            else if (radioButtonForKids.isChecked())
                TypeFor = "Kids";
            else
                TypeFor = "Womens";

            DataRecyclerviewItem data = new DataRecyclerviewItem(

                    category,
                    null,
                    title,
                    price,
                    TypeFor
            );

            addToShop.addItemToShop(data, TypeFor, selectedImage, AddItemActivity.this);
        });
        
    }

    Uri selectedImage;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_GALLERY) {
                selectedImage = data.getData();
                loading_image.setImageURI(selectedImage);
            }
        }
    }



}