package com.eibrahim.winkel.secondActivity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eibrahim.winkel.R;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.FrameLayoutProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int currentPage = getIntent().getIntExtra("state", 0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (currentPage) {
            case 0:
            {
                AccountFragment accountFragment = new AccountFragment();
                fragmentTransaction.replace(R.id.FrameLayoutProfile, accountFragment);
                break;
            }
            case 1:{
                myPaymentMethodsFragment myPaymentMethodsFragment = new myPaymentMethodsFragment();
                fragmentTransaction.replace(R.id.FrameLayoutProfile, myPaymentMethodsFragment);
                break;
            }
            case 2:{
                OrdersFragment ordersFragment = new OrdersFragment();
                fragmentTransaction.replace(R.id.FrameLayoutProfile, ordersFragment);
                break;
            }
            case 3:
                AddItemFragment addItemFragment = new AddItemFragment();
                fragmentTransaction.replace(R.id.FrameLayoutProfile, addItemFragment);
                break;
            default:
                Toast.makeText(this, "This page is not supported.", Toast.LENGTH_SHORT).show();

        }
        fragmentTransaction.commitNow();

    }
}