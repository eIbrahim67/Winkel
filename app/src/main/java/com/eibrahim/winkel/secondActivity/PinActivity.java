package com.eibrahim.winkel.secondActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eibrahim.winkel.R;

public class PinActivity extends AppCompatActivity {

    private String pin = "";

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

        View pin1 = findViewById(R.id.pin_p1);
        View pin2 = findViewById(R.id.pin_p2);
        View pin3 = findViewById(R.id.pin_p3);
        View pin4 = findViewById(R.id.pin_p4);

        Intent intent = new Intent(this, SecondActivity.class);


        int Goto = getIntent().getIntExtra("goto", 2);

        View.OnClickListener numberClickListener = v -> {
            Button button = (Button) v;
            String digit = button.getText().toString();
            pin += digit;

            if (pin.length() == 1){
                pin1.setBackgroundResource(R.drawable.background_rounded_blue_v2);
            }
            else if (pin.length() == 2){
                pin2.setBackgroundResource(R.drawable.background_rounded_blue_v2);
            }
            else if (pin.length() == 3){
                pin3.setBackgroundResource(R.drawable.background_rounded_blue_v2);
            }
            else if (pin.length() == 4){
                pin4.setBackgroundResource(R.drawable.background_rounded_blue_v2);

                if (Integer.parseInt(pin) == 1234){

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
                    pin1.setBackgroundResource(R.drawable.background_rounded_gray_v10);
                    pin2.setBackgroundResource(R.drawable.background_rounded_gray_v10);
                    pin3.setBackgroundResource(R.drawable.background_rounded_gray_v10);
                    pin4.setBackgroundResource(R.drawable.background_rounded_gray_v10);

                    Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();

                    pin = "";
                }

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
                    pin1.setBackgroundResource(R.drawable.background_rounded_gray_v10);
                }
                else if (pin.length() == 2){
                    pin2.setBackgroundResource(R.drawable.background_rounded_gray_v10);
                }
                else if (pin.length() == 3){
                    pin3.setBackgroundResource(R.drawable.background_rounded_gray_v10);
                }

                pin = pin.substring(0, pin.length() - 1);
            }

        });
    }

}