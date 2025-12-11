package com.eibrahim.winkel.item.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.payment.PaymentActivity;

public class AddedToBasketDialog extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.dialog_added_to_basket_dialog, container, false);

        ConstraintLayout done_dialog_btn = root.findViewById(R.id.done_dialog_btn);
        ConstraintLayout view_basket_dialog_btn = root.findViewById(R.id.view_basket_dialog_btn);


        done_dialog_btn.setOnClickListener(c -> {

            dismiss();

        });
        view_basket_dialog_btn.setOnClickListener(v -> {

            dismiss();
            startActivity(new Intent(requireContext(), PaymentActivity.class));
            requireActivity().finish();

        });

        return root;

    }

}
