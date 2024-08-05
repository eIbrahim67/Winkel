package com.eibrahim.winkel.mainPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.CreateBasketRef;
import com.eibrahim.winkel.declaredClasses.CreateOrderRef;
import com.eibrahim.winkel.declaredClasses.CreateWishlistRef;
import com.eibrahim.winkel.secondPages.sign.SigninActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    public static ChipNavigationBar chipNavigationBar;
    private HomeFragment homeFragment;
    private WishlistFragment wishlistFragment;
    private CheckoutFragment checkoutFragment;
    private ProfileFragment profileFragment;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final FragmentTransaction[] fragmentTransaction = {fragmentManager.beginTransaction()};
    private int LFrag = 0;
    public static Boolean basketClicked;

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

                    chipNavigationBarZero();

                }
                else if (i == R.id.wishlist_btn) {

                    chipNavigationBarOne();

                }
                else if(i == R.id.checkout_btn) {

                    chipNavigationBarTwo();

                }
                else if (i == R.id.profile_btn) {

                    chipNavigationBarThree();

                }
                else
                    throw new IllegalStateException("Unexpected value: " + i);

            });

        }

    }

    private void chipNavigationBarZero(){

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].show(homeFragment);
        fragmentTransaction[0].commit();

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].hide(getLFrag(LFrag));
        fragmentTransaction[0].commit();

        LFrag = 0;

    }

    private void chipNavigationBarOne(){

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].show(wishlistFragment);
        fragmentTransaction[0].commit();

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].hide(getLFrag(LFrag));
        fragmentTransaction[0].commit();

        LFrag = 1;

    }

    private void chipNavigationBarTwo(){

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].show(checkoutFragment);
        fragmentTransaction[0].commit();

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].hide(getLFrag(LFrag));
        fragmentTransaction[0].commit();

        LFrag = 2;

    }

    private void chipNavigationBarThree(){

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].show(profileFragment);
        fragmentTransaction[0].commit();

        fragmentTransaction[0] = fragmentManager.beginTransaction();
        fragmentTransaction[0].hide(getLFrag(LFrag));
        fragmentTransaction[0].commit();

        LFrag = 3;


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

    @Override
    protected void onResume() {
        super.onResume();

        if (basketClicked != null)
            if (basketClicked)
                chipNavigationBar.setItemSelected(R.id.checkout_btn, true);
    }
}