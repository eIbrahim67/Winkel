package com.eibrahim.winkel.mainPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.CreateBasketRef;
import com.eibrahim.winkel.declaredClasses.CreateOrderRef;
import com.eibrahim.winkel.declaredClasses.CreateWishlistRef;
import com.eibrahim.winkel.sign.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    public static ChipNavigationBar chipNavigationBar;

    private HomeFragment homeFragment;
    private WishlistFragment wishlistFragment;
    private CheckoutFragment checkoutFragment;
    private ProfileFragment profileFragment;

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
            chipNavigationBar = findViewById(R.id.main_menu);
            FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction[] fragmentTransaction = {fragmentManager.beginTransaction()};

            CreateOrderRef createOrderRef = new CreateOrderRef();
            CreateBasketRef createBasketRef = new CreateBasketRef();
            CreateWishlistRef createWishlistRef = new CreateWishlistRef();

            createOrderRef.createIt();
            createBasketRef.createIt();
            createWishlistRef.createIt();

            homeFragment = new HomeFragment();
            wishlistFragment = new WishlistFragment();
            checkoutFragment = new CheckoutFragment();
            profileFragment = new ProfileFragment();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout, wishlistFragment);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].hide(wishlistFragment);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout, checkoutFragment);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].hide(checkoutFragment);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout, profileFragment);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].hide(profileFragment);
            fragmentTransaction[0].commit();

            fragmentTransaction[0] = fragmentManager.beginTransaction();
            fragmentTransaction[0].add(R.id.MainActivity_layout, homeFragment);
            fragmentTransaction[0].commit();

            chipNavigationBar.setItemSelected(R.id.home_btn, true);

            chipNavigationBar.setOnItemSelectedListener(i -> {
                if (i == R.id.home_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(homeFragment);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 0;


                }
                else if (i == R.id.wishlist_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(wishlistFragment);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 1;

                }
                else if(i == R.id.checkout_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(checkoutFragment);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 2;


                }
                else if (i == R.id.profile_btn) {

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].show(profileFragment);
                    fragmentTransaction[0].commit();

                    fragmentTransaction[0] = fragmentManager.beginTransaction();
                    fragmentTransaction[0].hide(getLFrag(LFrag));
                    fragmentTransaction[0].commit();

                    LFrag = 3;


                }
                else
                    throw new IllegalStateException("Unexpected value: " + i);

            });

        }

    }
    private Fragment getLFrag(int i){
        if (i == 0)
            return homeFragment;
        if (i == 1)
            return wishlistFragment;
        if (i == 2)
            return checkoutFragment;
        else
            return profileFragment;

    }



}