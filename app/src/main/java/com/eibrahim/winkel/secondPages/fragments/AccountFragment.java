package com.eibrahim.winkel.secondPages.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eibrahim.winkel.R;

public class AccountFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

//        WebView masterCard = root.findViewById(R.id.master_card);
//        WebSettings webSettings = masterCard.getSettings();
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//
//        masterCard.loadUrl("file:///android_asset/masterCard/masterCard.html");

        return root;
    }

}