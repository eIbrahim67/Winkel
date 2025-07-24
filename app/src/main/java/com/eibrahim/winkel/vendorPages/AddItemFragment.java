package com.eibrahim.winkel.vendorPages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataProductItem;
import com.eibrahim.winkel.databinding.AddItemActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.util.HashMap;
import java.util.Map;

public class AddItemFragment extends Fragment {

    private AddItemActivityBinding binding;
    private Uri selectedImage;
    private String typeFor;
    private ProgressDialog progressDialog;

    private final ActivityResultLauncher<Intent> imageGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImage = result.getData().getData();
                    binding.loadingImage.setImageURI(selectedImage);
                }
            });

    private final ActivityResultLauncher<Intent> takePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImage = result.getData().getData();
                    binding.loadingImage.setImageURI(selectedImage);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AddItemActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.takePhoto.setOnClickListener(v -> dispatchTakePictureIntent());
        binding.uploadPhoto.setOnClickListener(v -> openGallery());

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.uploadBtn.setOnClickListener(v -> {
            String price = binding.postPrice.getText().toString();
            String category = binding.postDetails.getText().toString();
            String title = binding.postTitle.getText().toString();

            if (binding.forMen.isChecked()) {
                typeFor = "Mens";
            } else if (binding.forKids.isChecked()) {
                typeFor = "Kids";
            } else {
                typeFor = "Womens";
            }

            if (price.isEmpty() || category.isEmpty() || title.isEmpty() || selectedImage == null) {
                Toast.makeText(requireContext(), getText(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                DataProductItem data = new DataProductItem(category, null, title, price, typeFor);
                addItemToShop(data, typeFor, selectedImage);
            } catch (Exception e) {
                Toast.makeText(requireContext(), getString(R.string.unexpected_error_occurred), Toast.LENGTH_SHORT).show();
                Log.e("UploadError", "Error preparing item", e);
            }
        });
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageGalleryLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Gallery error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Camera error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void addItemToShop(DataProductItem item, String typeFor, Uri uri) {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference collectionRef = firestore.collection("Products")
                    .document(typeFor).collection(typeFor);
            DocumentReference documentRef = collectionRef.document();
            String documentId = documentRef.getId();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            item.setItemId(documentId);
            item.setUserId(userId);

            collectionRef.document(documentId).set(item)
                    .addOnSuccessListener(aVoid -> uploadImageToFirebase(uri, documentId, typeFor))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Failed to upload data", Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Upload error", e);
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Upload", "Error adding item", e);
        }
    }

    private void uploadImageToFirebase(Uri uri, String docId, String typeFor) {
        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            String imageName = System.currentTimeMillis() + ".png";
            StorageReference imageRef = storageRef.child("images of products/" + imageName);

            imageRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUrl ->
                                    updateImageUrlInFirestore(downloadUrl.toString(), docId, typeFor)))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseStorage", "Upload failed", e);
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "Unexpected storage error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImageUrlInFirestore(String imageUrl, String docId, String typeFor) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DocumentReference docRef = firestore.collection("Products")
                    .document(typeFor).collection(typeFor).document(docId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("imageId", imageUrl);

            docRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), R.string.item_uploaded_success, Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Image URL update failed", e);
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "Unexpected Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
