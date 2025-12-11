package com.eibrahim.winkel.addItem;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.adapterRecyclerviewCategoriesForAddItem;
import com.eibrahim.winkel.databinding.AddItemActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddItemFragment extends Fragment {

    private AddItemActivityBinding binding;
    private Uri selectedImage;
    private Uri photoUri;
    private String typeFor;
    private adapterRecyclerviewCategoriesForAddItem adapter;

    private final List<String> categoriesList = new ArrayList<>();
    private final CollectionReference colReference = FirebaseFirestore.getInstance().collection("Data");

    private String currentPermission = null;

    private final ActivityResultLauncher<Intent> imageGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            selectedImage = result.getData().getData();
            binding.loadingImage.setImageURI(selectedImage);
        }
    });

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            selectedImage = photoUri;
            binding.loadingImage.setImageURI(selectedImage);
        }
    });

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
        if (granted) {
            if (Manifest.permission.CAMERA.equals(currentPermission)) dispatchTakePictureIntent();
            else if (getGalleryPermission().equals(currentPermission)) openGallery();
        } else if (!shouldShowRequestPermissionRationale(currentPermission)) {
            showPermissionDeniedDialog();
        } else {
            Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddItemActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new adapterRecyclerviewCategoriesForAddItem(categoriesList);
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        binding.recyclerviewType.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.takePhoto.setOnClickListener(v -> checkCameraPermission());
        binding.uploadPhoto.setOnClickListener(v -> checkGalleryPermission());
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.uploadBtn.setOnClickListener(v -> uploadItem());

        loadCategories("Mens");

        binding.forMen.setOnClickListener(v -> {
            loadCategories("Mens");
        });

        binding.forWomen.setOnClickListener(v -> {
            loadCategories("Womens");
        });

        binding.forKids.setOnClickListener(v -> {
            loadCategories("Kids");
        });

    }

    private void loadCategories(String gender) {
        colReference.document("Categories" + gender).get().addOnSuccessListener(doc -> {
            categoriesList.clear();
            if (doc.exists() && doc.get("Categories") != null) {
                List<?> data = (List<?>) doc.get("Categories");
                for (Object item : data) categoriesList.add(item.toString());
            }
            if (!categoriesList.isEmpty()) {
                categoriesList.remove(0);
                binding.recyclerviewType.setAdapter(adapter);
            }

        });
    }

    private void checkCameraPermission() {
        currentPermission = Manifest.permission.CAMERA;
        handlePermission(currentPermission, getString(R.string.camera_permission_required), getString(R.string.we_need_access_to_your_camera_so_you_can_take_a_photo_of_the_item));
    }

    private void checkGalleryPermission() {
        currentPermission = getGalleryPermission();
        handlePermission(currentPermission, getString(R.string.storage_permission_required), getString(R.string.we_need_access_to_your_photos_so_you_can_upload_an_item_image));
    }

    private void handlePermission(String permission, String title, String message) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            if (Manifest.permission.CAMERA.equals(permission)) dispatchTakePictureIntent();
            else openGallery();
        } else if (shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(requireContext()).setTitle(title).setMessage(message).setPositiveButton(R.string.grant, (d, w) -> permissionLauncher.launch(permission)).setNegativeButton(R.string.cancel, (d, w) -> Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()).setCancelable(false).show();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private String getGalleryPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext()).setTitle(R.string.permission_denied).setMessage(R.string.you_have_permanently_denied_this_permission_to_enable_it_go_to_settings_app_permissions).setPositiveButton(R.string.open_settings, (d, w) -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
            startActivity(intent);
        }).setNegativeButton(R.string.cancel, (d, w) -> d.dismiss()).setCancelable(false).show();
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Gallery error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            File photoFile = File.createTempFile("IMG_", ".jpg", requireContext().getExternalFilesDir(null));
            photoUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            takePhotoLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Camera error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("error", e.getMessage().toString());
        }
    }

    private void uploadItem() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.uploadBtn.setVisibility(View.GONE);
        String price = binding.postPrice.getText().toString().trim();
        String title = binding.postTitle.getText().toString().trim();
        String details = binding.postDetails.getText().toString().trim();
        String category = adapter.getSelected();

        typeFor = binding.forMen.isChecked() ? "Mens" : binding.forKids.isChecked() ? "Kids" : binding.forWomen.isChecked() ? "Womens" : null;

// Validate each field individually
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_enter_title, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            return;
        }

        if (details.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_enter_details, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            return;
        }

        if (typeFor == null) {
            Toast.makeText(requireContext(), R.string.error_select_type_for, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            return;
        }

        if (category == null) {
            Toast.makeText(requireContext(), R.string.error_select_category, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            return;
        }

        if (price.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_enter_price, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            return;
        }

        if (selectedImage == null) {
            Toast.makeText(requireContext(), R.string.error_select_image, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            return;
        }

        DataProductItem item = new DataProductItem(category, null, title, price, typeFor);
        binding.progressBar.setVisibility(View.GONE);
        binding.uploadBtn.setVisibility(View.VISIBLE);
        item.setDetails(details);
        addItemToShop(item, selectedImage);
    }

    private void addItemToShop(DataProductItem item, Uri imageUri) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collection = firestore.collection("Products").document(typeFor).collection(typeFor);
        DocumentReference docRef = collection.document();
        String docId = docRef.getId();

        item.setItemId(docId);
        item.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());

        collection.document(docId).set(item).addOnSuccessListener(aVoid -> uploadImageToFirebase(imageUri, docId)).addOnFailureListener(e -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), R.string.failed_to_upload_data, Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImageToFirebase(Uri uri, String docId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("images of products/" + System.currentTimeMillis() + ".png");

        ref.putFile(uri).addOnSuccessListener(task -> ref.getDownloadUrl().addOnSuccessListener(url -> updateImageUrlInFirestore(url.toString(), docId))).addOnFailureListener(e -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), R.string.image_upload_failed, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateImageUrlInFirestore(String url, String docId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Products").document(typeFor).collection(typeFor).document(docId).update("imageId", url).addOnSuccessListener(aVoid -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), R.string.item_uploaded_success, Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }).addOnFailureListener(e -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.uploadBtn.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), R.string.unexpected_error_occurred, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
