package com.eibrahim.winkel.imagePreview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.FragmentImagePreviewBinding;
import com.eibrahim.winkel.helper.RemoveBgHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagePreviewFragment extends Fragment {

    private FragmentImagePreviewBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ImagePreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentImagePreviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey("image_uri")) {
            String path = args.getString("image_uri");
            if (path != null) {
                Uri uri = Uri.parse(path);

                // Load image safely with Glide
                Glide.with(this).load(uri).into(binding.imageView);

                binding.removeBackground.setOnClickListener(remove -> {
                    binding.removeBackground.setEnabled(false);
                    binding.removeBackground.setText("");
                    binding.progressBar.setVisibility(View.VISIBLE);

                    executor.execute(() -> {
                        try {
                            File imageFile = uriToFile(uri);
                            if (imageFile == null) {
                                showError("Failed to convert Uri to File");
                                return;
                            }

                            RemoveBgHelper.removeBackground(imageFile, imageBytes -> {
                                if (imageBytes != null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    requireActivity().runOnUiThread(() -> {
                                        binding.imageView.setImageBitmap(bmp);
                                        resetButton();

                                        binding.done.setOnClickListener(done -> {

                                            File newImageFile = saveBitmapToTempFile(bmp);
                                            if (newImageFile != null) {
                                                Bundle result = new Bundle();
                                                result.putString("image_uri", Uri.fromFile(newImageFile).toString());
                                                getParentFragmentManager().setFragmentResult("image_preview_result", result);

                                                // Go back to previous fragment
                                                requireActivity().getSupportFragmentManager().popBackStack();
                                            }

                                        });

                                    });
                                } else {
                                    showError("Background removal failed!");
                                }
                            });

                        } catch (Exception e) {
                            showError("Error: " + e.getMessage());
                        }
                    });
                });
            }
        }
    }

    private File saveBitmapToTempFile(Bitmap bitmap) {
        try {
            File tempFile = File.createTempFile("bg_removed", ".png", requireContext().getCacheDir());
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            return tempFile;
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
            return null;
        }
    }


    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        requireActivity().runOnUiThread(this::resetButton);
    }

    private void resetButton() {
        binding.removeBackground.setEnabled(true);
        binding.removeBackground.setText(R.string.remove_background);
        binding.progressBar.setVisibility(View.GONE);
    }

    private File uriToFile(Uri uri) {
        try {
            File tempFile = File.createTempFile("temp_image", ".jpg", requireContext().getCacheDir());
            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri); FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                if (inputStream != null) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }
            return tempFile;
        } catch (Exception e) {
            Snackbar.make(binding.getRoot(), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executor.shutdown();
    }
}
