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
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterRecyclerviewOrders extends RecyclerView.Adapter<AdapterRecyclerviewOrders.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewItemOrder> itemList;

    private int counter = 1;

    public AdapterRecyclerviewOrders(Context context, List<DataRecyclerviewItemOrder> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ShapeableImageView itemImage;
        final TextView orderId;
        final TextView orderCustId;
        final TextView orderFarmerId;
        final TextView orderTotalPrice;
        final TextView orderCounter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImgCheck);
            orderId = itemView.findViewById(R.id.orderId);
            orderCustId = itemView.findViewById(R.id.orderCustId);
            orderFarmerId = itemView.findViewById(R.id.orderFarmerId);
            orderTotalPrice = itemView.findViewById(R.id.orderTotalPrice);
            orderCounter = itemView.findViewById(R.id.orderCounter);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_orders_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerviewItemOrder currentItem = itemList.get(position);

        holder.orderId.setText(currentItem.getOrderId());
        holder.orderCustId.setText(currentItem.getCustId());
        holder.orderFarmerId.setText(currentItem.getFarmerId());
        holder.orderTotalPrice.setText(currentItem.getTotalPrice());
        holder.orderCounter.setText(String.valueOf(counter));
        counter++;

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
