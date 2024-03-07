package com.eibrahim.winkel.mianActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.sign.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    public ChipNavigationBar chipNavigationBar;

    private HomeFragment h;
    private WishlistFragment w;
    private CheckoutFragment c;
    private ProfileFragment p;

    private int LFrag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
        }
        else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction[] fragmentTransaction = {fragmentManager.beginTransaction()};

            h = new HomeFragment();
            w = new WishlistFragment();
            c = new CheckoutFragment();
            p = new ProfileFragment();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout, w);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].hide(w);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout,c );
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].hide(c);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout,p );
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].hide(p);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout,h );
            fragmentTransaction[0].commit();


            chipNavigationBar = findViewById(R.id.main_menu);

            chipNavigationBar.setItemSelected(R.id.home_btn, true);

            chipNavigationBar.setOnItemSelectedListener(i -> {
                if (i == R.id.home_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(h);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 0;


                }
                else if (i == R.id.wishlist_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(w);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 1;

                }
                else if(i == R.id.checkout_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(c);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 2;


                }
                else if (i == R.id.profile_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(p);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 3;


                }
                else
                    throw new IllegalStateException("Unexpected value: " + String.valueOf(i));

            });

        }


    }
    private Fragment getLFrag(int i){
        if (i == 0)
            return h;
        if (i == 1)
            return w;
        if (i == 2)
            return c;
        else
            return p;

    }



}