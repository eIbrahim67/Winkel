package com.eibrahim.winkel.addItem;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.AddItemActivityBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddItemFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private AddItemActivityBinding binding;
    private Uri selectedImage;
    private Uri photoUri; // for camera capture
    private String typeFor;
    private ProgressDialog progressDialog;

    // ---------------- Activity Result Launchers ----------------
    private final ActivityResultLauncher<Intent> imageGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImage = result.getData().getData();
                    binding.loadingImage.setImageURI(selectedImage);
                }
            });

    private final ActivityResultLauncher<Intent> takePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    selectedImage = photoUri;
                    binding.loadingImage.setImageURI(selectedImage);
                }
            });

    private String currentPermission = null;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    if (Manifest.permission.CAMERA.equals(currentPermission)) {
                        dispatchTakePictureIntent();
                    } else if (getGalleryPermission().equals(currentPermission)) {
                        openGallery();
                    }
                } else if (!shouldShowRequestPermissionRationale(currentPermission)) {
                    showPermissionDeniedDialog();
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    // ---------------- Lifecycle ----------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = AddItemActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        binding.takePhoto.setOnClickListener(v -> checkCameraPermission());
        binding.uploadPhoto.setOnClickListener(v -> checkGalleryPermission());
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

    // ---------------- Permissions ----------------
    private void checkCameraPermission() {
        currentPermission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(requireContext(), currentPermission) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else if (shouldShowRequestPermissionRationale(currentPermission)) {
            showPermissionRationaleDialog("Camera Permission Required",
                    "We need access to your camera so you can take a photo of the item.");
        } else {
            permissionLauncher.launch(currentPermission);
        }
    }

    private void checkGalleryPermission() {
        currentPermission = getGalleryPermission();
        if (ContextCompat.checkSelfPermission(requireContext(), currentPermission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (shouldShowRequestPermissionRationale(currentPermission)) {
            showPermissionRationaleDialog("Storage Permission Required",
                    "We need access to your photos so you can upload an item image.");
        } else {
            permissionLauncher.launch(currentPermission);
        }
    }

    private String getGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    private void showPermissionRationaleDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Grant", (dialog, which) -> permissionLauncher.launch(currentPermission))
                .setNegativeButton("Cancel", (dialog, which) ->
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                )
                .setCancelable(false)
                .show();
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Denied")
                .setMessage("You have permanently denied this permission. To enable it, go to Settings > App permissions.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    // ---------------- Intents ----------------
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
            File photoFile = File.createTempFile("IMG_", ".jpg",
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            photoUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".provider", photoFile);

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            takePhotoLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Camera error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------- Firebase ----------------
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
