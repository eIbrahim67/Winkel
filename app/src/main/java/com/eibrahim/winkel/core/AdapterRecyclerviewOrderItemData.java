package com.eibrahim.winkel.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;

import java.util.List;

public class AdapterRecyclerviewOrderItemData extends RecyclerView.Adapter<AdapterRecyclerviewOrderItemData.ViewHolder> {

    private final List<DataRecyclerviewItemOrderItemData> itemList;


    public AdapterRecyclerviewOrderItemData(List<DataRecyclerviewItemOrderItemData> itemList) {
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView ItemId, itemPrice, itemMuch, itemTotalPrice, itemSize, itemCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ItemId = itemView.findViewById(R.id.ItemId);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemMuch = itemView.findViewById(R.id.itemMuch);
            itemTotalPrice = itemView.findViewById(R.id.itemTotalPrice);
            itemSize = itemView.findViewById(R.id.itemSize);
            itemCategory = itemView.findViewById(R.id.itemCategory);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_orders_item_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerviewItemOrderItemData currentItem = itemList.get(position);

        holder.ItemId.setText(currentItem.getItemId());
        holder.itemPrice.setText(currentItem.getItemPrice());
        holder.itemMuch.setText(currentItem.getItemMuch());
        holder.itemTotalPrice.setText(currentItem.getItemTotalPrice());
        holder.itemSize.setText(currentItem.getItemSize());
        holder.itemCategory.setText(currentItem.getItemCategory());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
