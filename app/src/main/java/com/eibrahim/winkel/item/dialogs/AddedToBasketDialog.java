package com.eibrahim.winkel.item.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.eibrahim.winkel.R;

public class AddedToBasketDialog extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View  root = inflater.inflate(R.layout.dialog_added_to_basket_dialog, container, false);

        TextView done_dialog_btn = root.findViewById(R.id.done_dialog_btn);
        TextView view_basket_dialog_btn = root.findViewById(R.id.view_basket_dialog_btn);


        done_dialog_btn.setOnClickListener(c -> {

            dismiss();

        });

        view_basket_dialog_btn.setOnClickListener(v ->{



        } );

        return  root;

    }

}
