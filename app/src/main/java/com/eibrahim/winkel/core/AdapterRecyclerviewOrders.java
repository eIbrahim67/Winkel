package com.eibrahim.winkel.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;

import java.util.List;

public class AdapterRecyclerviewOrders extends RecyclerView.Adapter<AdapterRecyclerviewOrders.ViewHolder> {

    private Context context;
    private final List<DataOrderItem> itemList;

    private int counter = 1;

    public AdapterRecyclerviewOrders(List<DataOrderItem> itemList) {
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView orderCustId;
        final RecyclerView rv_orders_item_data;
        final TextView orderTotalPrice;
        final TextView orderCounter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderCustId = itemView.findViewById(R.id.orderCustId);
            orderTotalPrice = itemView.findViewById(R.id.orderTotalPrice);
            orderCounter = itemView.findViewById(R.id.orderCounter);
            rv_orders_item_data = itemView.findViewById(R.id.rv_orders_item_data);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_orders_items, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataOrderItem currentItem = itemList.get(position);

        holder.orderCustId.setText(currentItem.getCustId());
        holder.orderTotalPrice.setText(currentItem.getTotalPrice());
        holder.orderCounter.setText(String.valueOf(counter));
        counter++;

        AdapterRecyclerviewOrderItemData adapterRecyclerviewOrderItemData = new AdapterRecyclerviewOrderItemData(
                currentItem.getListItemsData()
        );

        holder.rv_orders_item_data.setLayoutManager(new LinearLayoutManager(context));

        holder.rv_orders_item_data.setAdapter(adapterRecyclerviewOrderItemData);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
