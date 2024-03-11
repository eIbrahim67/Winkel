package com.eibrahim.winkel.secondActivity;

import static android.content.ContentValues.TAG;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.declaredClasses.AddToShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import android.net.Uri;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;

public class AddItemtFragment extends Fragment {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private ShapeableImageView loading_image;
    private EditText item_category;
    private EditText item_title;
    private EditText item_price;
    private RadioButton radioButtonForMen, radioButtonForWomen, radioButtonForKids;
    String title, price, category;
    String TypeFor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.add_itemt_fragment, container, false);

        loading_image = root.findViewById(R.id.loading_image);
        item_category = root.findViewById(R.id.post_details);
        item_title = root.findViewById(R.id.post_title);
        item_price = root.findViewById(R.id.post_price);

        radioButtonForMen = root.findViewById(R.id.for_men);
        radioButtonForWomen = root.findViewById(R.id.for_women);
        radioButtonForKids = root.findViewById(R.id.for_kids);

        TextView upload_btn = root.findViewById(R.id.upload_btn);
        RelativeLayout upload_photo = root.findViewById(R.id.upload_photo);
        RelativeLayout take_photo = root.findViewById(R.id.take_photo);


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

            AddToShop.addItemToShop(data, TypeFor, selectedImage, AddItemtFragment.this);
        });

        return root;
    }

    Uri selectedImage;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_GALLERY) {
                selectedImage = data.getData();
                loading_image.setImageURI(selectedImage);
            }
        }
    }



}