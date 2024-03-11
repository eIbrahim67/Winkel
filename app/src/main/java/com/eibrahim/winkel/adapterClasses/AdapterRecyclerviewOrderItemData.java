package com.eibrahim.winkel.adapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItemOrder;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItemOrderItemData;

import java.util.List;

public class AdapterRecyclerviewOrderItemData extends RecyclerView.Adapter<AdapterRecyclerviewOrderItemData.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewItemOrderItemData> itemList;


    public AdapterRecyclerviewOrderItemData(Context context, List<DataRecyclerviewItemOrderItemData> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView ItemId;
        final TextView itemPrice;
        final TextView itemMuch;
        final TextView itemTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ItemId = itemView.findViewById(R.id.ItemId);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemMuch = itemView.findViewById(R.id.itemMuch);
            itemTotalPrice = itemView.findViewById(R.id.itemTotalPrice);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_orders_item_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerviewItemOrderItemData currentItem = itemList.get(position);

        holder.ItemId.setText(currentItem.getItemId());
        holder.itemPrice.setText(currentItem.getItemPrice());
        holder.itemMuch.setText(currentItem.getItemMuch());
        holder.itemTotalPrice.setText(currentItem.getItemTotalPrice());


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
