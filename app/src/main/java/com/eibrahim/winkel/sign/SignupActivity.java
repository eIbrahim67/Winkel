package com.eibrahim.winkel.sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.mainActivity.MainActivity;
import com.eibrahim.winkel.secondActivity.PaymentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SignupActivity extends AppCompatActivity {

    private EditText name;
    private EditText mail;
    private EditText pass;
    private EditText repass;
    private RadioButton admin;
    private RadioButton cust;
    private RadioButton vendor;
    private EditText phone;
    private LinearLayout code_layout;
    private LinearLayout license;
    private EditText code_text;
    private String username;
    private String password;
    private String rePassword;
    private String phoneNo;
    private String email;
    private String userType = "Customer";
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        mail = findViewById(R.id.email_signup);
        pass = findViewById(R.id.pass_signup);
        name = findViewById(R.id.name_signup);
        repass = findViewById(R.id.repass_signup);
        admin = findViewById(R.id.radio_admin);
        cust = findViewById(R.id.radio_cust);
        vendor = findViewById(R.id.radio_vendor);
        code_layout = findViewById(R.id.code_layout);
        code_text = findViewById(R.id.code_text);
        license = findViewById(R.id.license_add);
        phone = findViewById(R.id.phone_signup);

        Button btnSignup = findViewById(R.id.btn_signup);
        TextView btnSignin = findViewById(R.id.btn_signin2);
        CheckBox checkSignup = findViewById(R.id.check_signup);

        cust.setActivated(true);


        admin.setOnClickListener(v -> {
            vendor.setChecked(false);
            cust.setChecked(false);
            license.setVisibility(View.VISIBLE);
            code_layout.setVisibility(View.VISIBLE);
            userType = "Admin";
        });

        cust.setOnClickListener(v -> {
            admin.setChecked(false);
            vendor.setChecked(false);
            license.setVisibility(View.GONE);
            code_layout.setVisibility(View.GONE);
            userType = "Customer";
        });

        vendor.setOnClickListener(v -> {
            admin.setChecked(false);
            cust.setChecked(false);
            license.setVisibility(View.VISIBLE);
            code_layout.setVisibility(View.GONE);
            userType = "Vendor";
        });


        btnSignin.setOnClickListener(v -> finish());

        btnSignup.setOnClickListener(v -> {

            username = name.getText().toString();
            email = mail.getText().toString();
            password = pass.getText().toString();
            rePassword = repass.getText().toString();
            phoneNo = phone.getText().toString();

            if (username.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please enter a username.", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please enter an email address.", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            } else if (rePassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please re-enter the password.", Toast.LENGTH_SHORT).show();
            } else if (!rePassword.equals(password)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else if (phoneNo.length() != 13 || !phoneNo.startsWith("+")) {
                Toast.makeText(SignupActivity.this, "Please enter a valid phone number including country code.", Toast.LENGTH_SHORT).show();
            } else if (!checks()) {
                Toast.makeText(SignupActivity.this, "Invalid user type.", Toast.LENGTH_SHORT).show();
            } else if (!checkSignup.isChecked()) {
                Toast.makeText(SignupActivity.this, "Please agree to our terms of service.", Toast.LENGTH_SHORT).show();
            } else {
                createAccount(email, password);
            }

        });

    }

    private Boolean checks(){

         if (admin.isChecked()){
             return code_text.getText().toString().equals("admin44");
        } else if (vendor.isChecked()) {
             return code_text.getText().toString().equals("vendor99");
         } else return true;
    }

    private void createAccount(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Creation Successful! Your account has been created.", Toast.LENGTH_SHORT).show();
                        loginUser(email, password);
                    } else {
                        Toast.makeText(SignupActivity.this, "Creation failed. Please double-check your data and try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Authentication Successful! Welcome aboard!", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(SignupActivity.this, "Authentication failed. Please double-check your information and try again.", Toast.LENGTH_SHORT).show();
                    }
                });

        String userId = String.valueOf(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentRef = firestore
                .collection("UsersData")
                .document(userId)
                .collection("UserPersonalData")
                .document("UserPersonalData");

        Map<String, String> data = new HashMap<>();
        data.put("userName", username);
        data.put("phoneNo", phoneNo);
        data.put("userType", userType);

        documentRef.set(data)
                .addOnSuccessListener(aVoid ->{
                        Log.d("Firestore", "Document added/updated successfully");
                    finish();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding/updating document", e)
                );
    }
}