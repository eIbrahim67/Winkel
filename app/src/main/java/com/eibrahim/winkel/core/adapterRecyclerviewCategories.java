package com.eibrahim.winkel.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewCategories extends RecyclerView.Adapter<adapterRecyclerviewCategories.ViewHolder> {

    private final List<String> itemList;
    private final RecyclerView recyclerViewItems;
    private final String type;
    private final String tPrice;
    private final String fPrice;
    private final View skeletonLayout; // Injected skeleton
    private ViewHolder lastHolder = null;

    public adapterRecyclerviewCategories(
            List<String> itemList,
            RecyclerView recyclerViewItems,
            String type,
            String fPrice,
            String tPrice,
            Context context,
            View skeletonLayout // optional skeleton view
    ) {
        this.itemList = itemList;
        this.recyclerViewItems = recyclerViewItems;
        this.context = context;
        this.type = type;
        this.fPrice = fPrice;
        this.tPrice = tPrice;
        this.skeletonLayout = skeletonLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filters, parent, false);
        Context context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String filter = itemList.get(position);

        holder.itemCategoryType.setText(filter);
        holder.itemCategoryTypeSelected.setText(filter);

        // Default selection
        if (lastHolder == null && Objects.equals(filter, "All")) {
            holder.itemCategoryTypeSelected.setVisibility(View.VISIBLE);
            lastHolder = holder;
        }

        holder.itemView.setOnClickListener(v -> holder.itemCategoryType.callOnClick());
        holder.itemCategoryTypeSelected.setOnClickListener(v -> holder.itemCategoryType.callOnClick());

        holder.itemCategoryType.setOnClickListener(v -> {

            // Show skeleton if injected
            if (skeletonLayout != null) {
                skeletonLayout.setVisibility(View.VISIBLE);
            }

            // Clear old adapter to prevent flicker
            recyclerViewItems.setAdapter(null);

            // Fetch filtered data
            FetchDataFromFirebase fetchDataFromFirebase = new FetchDataFromFirebase(recyclerViewItems, context, (LinearLayout) skeletonLayout);
            if (Objects.equals(filter, "All")) {
                fetchDataFromFirebase.fetch(type, fPrice, tPrice);
            } else {
                fetchDataFromFirebase.fetch(type, filter, fPrice, tPrice);
            }

            // Update selection UI
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

    // Clear adapter data
    public void clear() {
        itemList.clear();
        notifyDataSetChanged();
    }
}
