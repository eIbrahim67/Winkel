package com.eibrahim.winkel.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewCategories extends RecyclerView.Adapter<adapterRecyclerviewCategories.ViewHolder> {

    private final List<String> itemList;
    private final RecyclerView recyclerView_items;
    private final Context context;
    private final String type;
    private final String tPrice;
    private final String fPrice;
    private ViewHolder lastHolder = null;

    public adapterRecyclerviewCategories(List<String> itemList, RecyclerView recyclerView_items, String type, String fPrice, String tPrice, Context context) {
        this.itemList = itemList;
        this.recyclerView_items = recyclerView_items;
        this.context = context;
        this.type = type;
        this.tPrice = tPrice;
        this.fPrice = fPrice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filters, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String filter = String.valueOf(itemList.get(position));

        holder.itemCategoryType.setText(filter);

        holder.itemCategoryTypeSelected.setText(filter);

        if (lastHolder == null && Objects.equals(filter, "All")){
            holder.itemCategoryTypeSelected.setVisibility(View.VISIBLE);
            lastHolder = holder;
        }


        holder.itemView.setOnClickListener(v -> holder.itemCategoryType.callOnClick());

        holder.itemCategoryTypeSelected.setOnClickListener(v -> holder.itemCategoryType.callOnClick());

        holder.itemCategoryType.setOnClickListener(v -> {

            FetchDataFromFirebase fetchDataFromFirebase = new FetchDataFromFirebase(
                    recyclerView_items,
                    context);

            if (Objects.equals(filter, "All")){
                fetchDataFromFirebase.fetchData(type, fPrice, tPrice);
            }else{
                fetchDataFromFirebase.fetchData(type, filter, fPrice, tPrice);
            }

            if (lastHolder != null)
                lastHolder.itemCategoryTypeSelected.setVisibility(View.GONE);

            lastHolder = holder;

            holder.itemCategoryTypeSelected.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView itemCategoryType;

        final TextView itemCategoryTypeSelected;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCategoryType = itemView.findViewById(R.id.itemCategoryType);
            itemCategoryTypeSelected = itemView.findViewById(R.id.itemCategoryTypeSelected);

        }
    }

}
