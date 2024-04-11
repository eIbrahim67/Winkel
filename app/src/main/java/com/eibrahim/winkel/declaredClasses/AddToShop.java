package com.eibrahim.winkel.declaredClasses;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.secondPages.AddItemActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddToShop {

     ProgressDialog progressDialog;
     AddItemActivity addItemActivity;
    public void addItemToShop(DataRecyclerviewItem Item, String TypeFor, Uri uri, AddItemActivity addItemActivity){

        this.addItemActivity = addItemActivity;

        progressDialog = new ProgressDialog(addItemActivity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = firestore.collection("Products").
                document(TypeFor).collection(TypeFor);
        DocumentReference documentRef = collectionRef.document();
        String documentId = documentRef.getId();
        Item.setItemId(documentId);
            collectionRef.document(documentId).set(Item)
        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document added with ID: "))
        .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));

        uploadImageToFirebase(uri, documentId, TypeFor);

    }

    private  void uploadImageToFirebase(Uri uri, String docId, String TypeFor) {
        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = fStorage.getReference();

        String imageName = System.currentTimeMillis() + ".png";
        StorageReference imageRef = storageReference.child("images of products/" + imageName);

        imageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> updateImageUrlInFirestore(downloadUrl.toString(), docId, TypeFor)))
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private  void updateImageUrlInFirestore(String newImageUrl, String docId, String TypeFor) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference usersCollection = firestore.collection("Products").document(TypeFor). collection(TypeFor).document(docId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("imageId", newImageUrl);

        usersCollection.update(updates)
                // Success message
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(addItemActivity, "The new item was uploaded successfully.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    addItemActivity.finish();
                })

// Failure message
                .addOnFailureListener(e -> {
                    Toast.makeText(addItemActivity, "An unexpected error occurred. The item was not uploaded.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });

    }

}
