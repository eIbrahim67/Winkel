package com.eibrahim.winkel.secondPages.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class PinActivity extends AppCompatActivity {

    private String pin = "";
    private Boolean wrong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        Button btnPin1 = findViewById(R.id.btn_pin_1);
        Button btnPin2 = findViewById(R.id.btn_pin_2);
        Button btnPin3 = findViewById(R.id.btn_pin_3);
        Button btnPin4 = findViewById(R.id.btn_pin_4);
        Button btnPin5 = findViewById(R.id.btn_pin_5);
        Button btnPin6 = findViewById(R.id.btn_pin_6);
        Button btnPin7 = findViewById(R.id.btn_pin_7);
        Button btnPin8 = findViewById(R.id.btn_pin_8);
        Button btnPin9 = findViewById(R.id.btn_pin_9);
        Button btnPin0 = findViewById(R.id.btn_pin_0);
        RelativeLayout btnDelete = findViewById(R.id.btn_pin_delete);

        ImageView pin1 = findViewById(R.id.pin_p1);
        ImageView pin2 = findViewById(R.id.pin_p2);
        ImageView pin3 = findViewById(R.id.pin_p3);
        ImageView pin4 = findViewById(R.id.pin_p4);

        Intent intent = new Intent(this, SecondActivity.class);

        DocumentReference pinRef = FirebaseFirestore.getInstance()
                .collection("UsersData").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .collection("UserPersonalData").document("UserPersonalData");

        int Goto = getIntent().getIntExtra("goto", 2);

        View.OnClickListener numberClickListener = v -> {
            Button button = (Button) v;
            String digit = button.getText().toString();
            pin += digit;

            if (wrong){

                wrong = false;

                pin1.setBackgroundResource(R.drawable.rounded_outline_black_solid_white);
                pin1.setImageDrawable(null);

                pin2.setBackgroundResource(R.drawable.rounded_outline_black_solid_white);
                pin2.setImageDrawable(null);

                pin3.setBackgroundResource(R.drawable.rounded_outline_black_solid_white);
                pin3.setImageDrawable(null);

                pin4.setBackgroundResource(R.drawable.rounded_outline_black_solid_white);
                pin4.setImageDrawable(null);

            }

            if (pin.length() == 1){

                pin1.setBackgroundResource(R.drawable.background_rounded_white_stroke_blue);
                pin1.setImageResource(R.drawable.star_icon_blue);

            }
            else if (pin.length() == 2){

                pin2.setBackgroundResource(R.drawable.background_rounded_white_stroke_blue);
                pin2.setImageResource(R.drawable.star_icon_blue);
            }
            else if (pin.length() == 3){

                pin3.setBackgroundResource(R.drawable.background_rounded_white_stroke_blue);
                pin3.setImageResource(R.drawable.star_icon_blue);
            }
            else if (pin.length() == 4){

                pin4.setBackgroundResource(R.drawable.background_rounded_white_stroke_blue);
                pin4.setImageResource(R.drawable.star_icon_blue);

                pinRef.get()
                        .addOnSuccessListener(documentSnapshot -> {

                            if (documentSnapshot.exists()){

                                String pinNum = Objects.requireNonNull(Objects.requireNonNull(documentSnapshot.getData()).get("pin")).toString();

                                if (pin.equals(pinNum)){

                                    pin1.setBackgroundResource(R.drawable.background_rounded_white_stroke_green);
                                    pin1.setImageResource(R.drawable.star_icon_green);

                                    pin2.setBackgroundResource(R.drawable.background_rounded_white_stroke_green);
                                    pin2.setImageResource(R.drawable.star_icon_green);

                                    pin3.setBackgroundResource(R.drawable.background_rounded_white_stroke_green);
                                    pin3.setImageResource(R.drawable.star_icon_green);

                                    pin4.setBackgroundResource(R.drawable.background_rounded_white_stroke_green);
                                    pin4.setImageResource(R.drawable.star_icon_green);


                                    if (Goto == 0){

                                        intent.putExtra("state", 0);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else if (Goto ==  1){
                                        intent.putExtra("state", 1);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                                else {

                                    wrong = true;

                                    pin1.setBackgroundResource(R.drawable.background_rounded_white_stroke_red);
                                    pin1.setImageResource(R.drawable.star_icon_red);

                                    pin2.setBackgroundResource(R.drawable.background_rounded_white_stroke_red);
                                    pin2.setImageResource(R.drawable.star_icon_red);

                                    pin3.setBackgroundResource(R.drawable.background_rounded_white_stroke_red);
                                    pin3.setImageResource(R.drawable.star_icon_red);

                                    pin4.setBackgroundResource(R.drawable.background_rounded_white_stroke_red);
                                    pin4.setImageResource(R.drawable.star_icon_red);

                                    Toast.makeText(PinActivity.this, "Wrong PIN", Toast.LENGTH_SHORT).show();

                                    pin = "";
                                }

                            }

                        });


            }
        };

        btnPin1.setOnClickListener(numberClickListener);
        btnPin2.setOnClickListener(numberClickListener);
        btnPin3.setOnClickListener(numberClickListener);
        btnPin4.setOnClickListener(numberClickListener);
        btnPin5.setOnClickListener(numberClickListener);
        btnPin6.setOnClickListener(numberClickListener);
        btnPin7.setOnClickListener(numberClickListener);
        btnPin8.setOnClickListener(numberClickListener);
        btnPin9.setOnClickListener(numberClickListener);
        btnPin0.setOnClickListener(numberClickListener);

        btnDelete.setOnClickListener(v -> {

            if (!pin.isEmpty()) {

                if (pin.length() == 1){
                    pin1.setBackgroundResource(R.drawable.rounded_outline_black_solid_white);
                    pin1.setImageDrawable(null);
                }
                else if (pin.length() == 2){
                    pin2.setBackgroundResource(R.drawable.rounded_outline_black_solid_white);
                    pin2.setImageDrawable(null);
                }
                else if (pin.length() == 3){
                    pin3.setBackgroundResource(R.drawable.rounded_outline_black_solid_white);
                    pin3.setImageDrawable(null);
                }

                pin = pin.substring(0, pin.length() - 1);
            }

        });
    }

}