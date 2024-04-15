package com.eibrahim.winkel.sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private EditText pin;
    private EditText repin;
    private RadioButton admin;
    private RadioButton cust;
    private RadioButton vendor;
    private EditText phone;
    private LinearLayout code_layout;
    private LinearLayout license;
    private EditText code_text;
    private String username;
    private String password;
    private String pinNum;
    private String repinNum;
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
        pin = findViewById(R.id.pin_signup);
        repin = findViewById(R.id.re_pin_signup);

        Button btnSignup = findViewById(R.id.btn_signup);
        TextView btnSignin = findViewById(R.id.btn_signin2);
        CheckBox checkSignup = findViewById(R.id.check_signup);

        cust.setActivated(true);

        pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() > 4){

                    Toast.makeText(SignupActivity.this, R.string.pin_length_error, Toast.LENGTH_SHORT).show();
                    pin.setText(s.toString().substring(0, 4));

                }

            }
        });

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
            pinNum = pin.getText().toString();
            repinNum = repin.getText().toString();

            if (username.isEmpty()) {
                Toast.makeText(SignupActivity.this, R.string.username_empty_error, Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(SignupActivity.this, R.string.email_empty_error, Toast.LENGTH_SHORT).show();
            } else if (pinNum.isEmpty()) {
                Toast.makeText(SignupActivity.this, R.string.pin_empty_error, Toast.LENGTH_SHORT).show();
            } else if (repinNum.isEmpty()) {
                Toast.makeText(SignupActivity.this, R.string.repin_empty_error, Toast.LENGTH_SHORT).show();
            }  else if (!pinNum.equals(repinNum)) {
                Toast.makeText(SignupActivity.this, R.string.pins_not_match_error, Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(SignupActivity.this, R.string.password_empty_error, Toast.LENGTH_SHORT).show();
            } else if (rePassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, R.string.re_password_empty_error, Toast.LENGTH_SHORT).show();
            } else if (!rePassword.equals(password)) {
                Toast.makeText(SignupActivity.this, R.string.passwords_not_match_error, Toast.LENGTH_SHORT).show();
            } else if (phoneNo.length() != 11) {
                Toast.makeText(SignupActivity.this, R.string.invalid_phone_error, Toast.LENGTH_SHORT).show();
            } else if (!checks()) {
                Toast.makeText(SignupActivity.this, R.string.invalid_user_type_error, Toast.LENGTH_SHORT).show();
            } else if (!checkSignup.isChecked()) {
                Toast.makeText(SignupActivity.this, R.string.terms_of_service_error, Toast.LENGTH_SHORT).show();
            }
            else {
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
                .addOnCompleteListener(authResult -> {
                    if (authResult.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, getText(R.string.creation_successful_message), Toast.LENGTH_SHORT).show();
                        loginUser(email, password);
                    } else {
                        Toast.makeText(SignupActivity.this, R.string.creation_failed_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, R.string.authentication_successful, Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(SignupActivity.this, R.string.authentication_failed_message, Toast.LENGTH_SHORT).show();
                    }
                });

        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

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
        data.put("pin", pinNum);

        documentRef.set(data)
                .addOnSuccessListener(aVoid ->{
                        Log.d("Firestore", "Document added/updated successfully");

                    Intent intent = new Intent();
                    intent.putExtra("mail", email);
                    intent.putExtra("pass", password);

                    finish();

                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding/updating document", e)
                );
    }
}