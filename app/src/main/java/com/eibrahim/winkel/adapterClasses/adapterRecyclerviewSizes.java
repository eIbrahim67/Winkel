package com.eibrahim.winkel.adapterClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;

import java.util.List;

public class adapterRecyclerviewSizes extends RecyclerView.Adapter<adapterRecyclerviewSizes.ViewHolder> {

    private final List<String> itemList;
    private ViewHolder lastHolder = null;
    private String lastSize = "null";

    public adapterRecyclerviewSizes(List<String> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_home_filters, parent, false);
        return new ViewHolder(view);
    }

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
            lastSize = filter;
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

    public String getSize(){

        return lastSize;

    }

}
