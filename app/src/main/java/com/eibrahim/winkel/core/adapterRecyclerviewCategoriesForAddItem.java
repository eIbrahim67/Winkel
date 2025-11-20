package com.eibrahim.winkel.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewCategoriesForAddItem extends RecyclerView.Adapter<adapterRecyclerviewCategoriesForAddItem.ViewHolder> {

    private final List<String> itemList;
    private ViewHolder lastHolder = null;

    public adapterRecyclerviewCategoriesForAddItem(List<String> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filters, parent, false);
        return new ViewHolder(view);
    }

    private String cateSelected = null;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String filter = String.valueOf(itemList.get(position));

        holder.itemCategoryType.setText(filter);

        holder.itemCategoryTypeSelected.setText(filter);

        holder.itemView.setOnClickListener(v -> holder.itemCategoryType.callOnClick());

        holder.itemCategoryTypeSelected.setOnClickListener(v -> holder.itemCategoryType.callOnClick());

        holder.itemCategoryType.setOnClickListener(v -> {

            if (lastHolder != null)
                lastHolder.itemCategoryTypeSelected.setVisibility(View.GONE);

            lastHolder = holder;
            cateSelected = filter;
            holder.itemCategoryTypeSelected.setVisibility(View.VISIBLE);
        });
    }

    public String getSelected() {
        return cateSelected;
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
