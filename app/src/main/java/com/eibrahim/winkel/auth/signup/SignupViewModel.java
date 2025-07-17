package com.eibrahim.winkel.auth.signup;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupViewModel extends AndroidViewModel {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SignupViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getSignupSuccess() {
        return signupSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void createAccount(String email, String password, String username, String phoneNo, String userType, String pinNum) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignupViewModel", "Account created");
                        saveUserData(username, phoneNo, userType, pinNum);
                    } else {
                        errorMessage.setValue("Failed to create account: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void saveUserData(String username, String phoneNo, String userType, String pinNum) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        DocumentReference documentRef = firestore.collection("UsersData")
                .document(userId).collection("UserPersonalData").document("UserPersonalData");

        Map<String, String> data = new HashMap<>();
        data.put("userName", username);
        data.put("phoneNo", phoneNo);
        data.put("userType", userType);
        data.put("pin", pinNum);

        documentRef.set(data)
                .addOnSuccessListener(aVoid -> signupSuccess.setValue(true))
                .addOnFailureListener(e -> errorMessage.setValue("Error saving data: " + e.getMessage()));
    }
}
