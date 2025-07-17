package com.eibrahim.winkel.bottomSheets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.addPaymentPage.addPaymentMethodActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class addMethodBottomSheet extends BottomSheetDialogFragment {

    private final Context context;

    public addMethodBottomSheet(Context context) {

        this.context = context;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View  root = inflater.inflate(R.layout.bottom_sheet_add_method, container, false);

        RelativeLayout visa_btn =  root.findViewById(R.id.visa_btn);
        RelativeLayout mastercard_btn =  root.findViewById(R.id.mastercard_btn);
        Intent intent = new Intent(context, addPaymentMethodActivity.class);

        visa_btn.setOnClickListener(v -> {

            dismiss();

            intent.putExtra("type", "visa");
            startActivity(intent);

        });

        mastercard_btn.setOnClickListener(v -> {

            dismiss();

            intent.putExtra("type", "mastercard");
            startActivity(intent);

        });


        return  root;

    }


    

}