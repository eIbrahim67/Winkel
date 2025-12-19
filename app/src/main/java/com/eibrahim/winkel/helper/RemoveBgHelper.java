package com.eibrahim.winkel.helper;

import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import okhttp3.*;

public class RemoveBgHelper {

    private static final String API_KEY = "2nkyVHiv9aaC12vUyKqftHnr";

    public static void removeBackground(File imageFile, RemoveBgCallback callback) {
        new AsyncTask<File, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(File... files) {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                                "image_file",
                                imageFile.getName(),
                                RequestBody.create(MediaType.parse("image/png"), imageFile)
                        )
                        .addFormDataPart("size", "auto")
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.remove.bg/v1.0/removebg")
                        .addHeader("X-Api-Key", API_KEY)
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        return response.body().bytes();
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(byte[] result) {
                callback.onResult(result);
            }
        }.execute(imageFile);
    }

    public interface RemoveBgCallback {
        void onResult(byte[] imageBytes); // imageBytes will be PNG data with transparent background
    }
}
