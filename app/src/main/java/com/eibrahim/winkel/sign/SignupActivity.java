package com.eibrahim.winkel.sign;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


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
    private LinearLayout lincese;
    private EditText code_text;
    private String username;
    private String password;
    private String repassword;
    private String phoneNo;
    private String email;
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
        lincese = findViewById(R.id.licnese_add);
        phone = findViewById(R.id.phone_signup);

        Button btnSignup = findViewById(R.id.btn_signup);
        TextView btnSignin = findViewById(R.id.btn_signin2);
        CheckBox checkSignup = findViewById(R.id.check_signup);

        cust.setActivated(true);


        admin.setOnClickListener(v -> {
            vendor.setChecked(false);
            cust.setChecked(false);
            lincese.setVisibility(View.VISIBLE);
            code_layout.setVisibility(View.VISIBLE);
        });

        cust.setOnClickListener(v -> {
            admin.setChecked(false);
            vendor.setChecked(false);
            lincese.setVisibility(View.GONE);
            code_layout.setVisibility(View.GONE);
        });

        vendor.setOnClickListener(v -> {
            admin.setChecked(false);
            cust.setChecked(false);
            lincese.setVisibility(View.VISIBLE);
            code_layout.setVisibility(View.GONE);
        });


        btnSignin.setOnClickListener(v -> finish());

        btnSignup.setOnClickListener(v -> {

            username = name.getText().toString();
            email = mail.getText().toString();
            password = pass.getText().toString();
            repassword = repass.getText().toString();
            phoneNo = phone.getText().toString();

            if(username.isEmpty()){
                Toast.makeText(SignupActivity.this, "Add username", Toast.LENGTH_SHORT).show();

            }else if (email.isEmpty()){
                Toast.makeText(SignupActivity.this, "Add email", Toast.LENGTH_SHORT).show();
            }else if (password.isEmpty()){
                Toast.makeText(SignupActivity.this, "Add password", Toast.LENGTH_SHORT).show();
            }else if (repassword.isEmpty()){
                Toast.makeText(SignupActivity.this, "Add repassword", Toast.LENGTH_SHORT).show();
            }else if (!repassword.equals(password)){
                Toast.makeText(SignupActivity.this, "password and repassword are not the same", Toast.LENGTH_SHORT).show();
            }else if (phoneNo.length() != 13 && phoneNo.contains("+")){
                Toast.makeText(SignupActivity.this, "check from your phone no.", Toast.LENGTH_SHORT).show();
            }else if (!checkes()){
                Toast.makeText(SignupActivity.this, "error in user type.", Toast.LENGTH_SHORT).show();
            }else if (!checkSignup.isChecked()){
                Toast.makeText(SignupActivity.this, "You should agreed on our terms of services", Toast.LENGTH_SHORT).show();
            }
            else {

                createAccount(email, password);
            }
        });

    }

    private Boolean checkes(){

         if (admin.isChecked()){
             return code_text.getText().toString().equals("admin44");
        }
        else return true;
    }

    private void createAccount(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Creation Successful",Toast.LENGTH_SHORT).show();
                        loginUser(email, password);
                    } else {
                        Toast.makeText(SignupActivity.this, "Creation failed,\nPlease check from your data",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Authentication Successful",Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(SignupActivity.this, "Authentication failed,\nPlease check from your data",Toast.LENGTH_SHORT).show();
                    }
                });

        String userId = String.valueOf(auth.getCurrentUser().getUid());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentRef = firestore
                .collection("UsersData")
                .document(userId)
                .collection("UserPersonalData")
                .document("UserPersonalData");

        Map<String, String> data = new HashMap<>();
        data.put("userName", username);
        data.put("phoneNo", phoneNo);
        data.put("userType", "customer");

        documentRef.set(data)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document added/updated successfully"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding/updating document", e));
    }
}