package com.eibrahim.winkel.secondActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.R;

public class OrdersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        RecyclerView recyclerView_orders = root.findViewById(R.id.re_orders);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);

        recyclerView_orders.setLayoutManager(gridLayoutManager);



        return root;

    }
}