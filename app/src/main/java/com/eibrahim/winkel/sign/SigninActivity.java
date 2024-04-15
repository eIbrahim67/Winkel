package com.eibrahim.winkel.sign;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.mainPages.MainActivity;
import com.google.firebase.auth.FirebaseAuth;


public class SigninActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String username;
    private String password;


    @Override
    protected void onResume() {
        super.onResume();

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){

            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        EditText email = findViewById(R.id.email_signin);
        EditText pass = findViewById(R.id.pass_signin);
        Button btnSignin = findViewById(R.id.btn_signin);
        TextView btnSignup = findViewById(R.id.btn_signup2);

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
            startActivity(intent);
        });


        btnSignin.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                Toast.makeText(SigninActivity.this, getString(R.string.check_from_your_data),Toast.LENGTH_SHORT).show();
            } else {

                username = email.getText().toString();
                password = pass.getText().toString();

                loginUser(username, password);

            }
        });
    }

    private void loginUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SigninActivity.this, getString(R.string.authentication_successful),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {

                        Toast.makeText(SigninActivity.this, R.string.authentication_failed_message,Toast.LENGTH_SHORT).show();
                    }
                });
    }
}