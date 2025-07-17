package com.eibrahim.winkel.mainPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.auth.signin.SigninActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
        }else
            setupNavigation();
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
            Log.d("MainActivity", "BottomNavigationView setup with NavController");
        } else {
            Log.e("MainActivity", "NavHostFragment is null!");
        }
    }
}

