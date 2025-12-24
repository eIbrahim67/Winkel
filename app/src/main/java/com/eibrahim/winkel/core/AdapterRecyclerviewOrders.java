package com.eibrahim.winkel.core;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ItemRvOrdersItemsBinding;

import java.util.List;

public class AdapterRecyclerviewOrders extends RecyclerView.Adapter<AdapterRecyclerviewOrders.ViewHolder> {

    private final List<DataOrderItem> itemList;

    public AdapterRecyclerviewOrders(List<DataOrderItem> itemList) {
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRvOrdersItemsBinding binding;
        private final AdapterRecyclerviewOrderItemData itemAdapter;

        public ViewHolder(ItemRvOrdersItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Initialize inner RecyclerView once
            binding.rvOrdersItemData.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            itemAdapter = new AdapterRecyclerviewOrderItemData();
            binding.rvOrdersItemData.setAdapter(itemAdapter);
        }

        public static String toRoman(int number) {
            int[] values = {
                    1000, 900, 500, 400,
                    100, 90, 50, 40,
                    10, 9, 5, 4, 1
            };

            String[] symbols = {
                    "M", "CM", "D", "CD",
                    "C", "XC", "L", "XL",
                    "X", "IX", "V", "IV", "I"
            };

            StringBuilder roman = new StringBuilder();

            for (int i = 0; i < values.length; i++) {
                while (number >= values[i]) {
                    number -= values[i];
                    roman.append(symbols[i]);
                }
            }
            return roman.toString();
        }

        public void bind(DataOrderItem orderItem, int position) {
            binding.orderCounter.setText(toRoman(position + 1));

            // Correct way to get string from resources
            String orderNumber = binding.getRoot().getContext().getString(R.string.order_number, position + 1);
            binding.orderCustId.setText(orderNumber);

            // Update inner adapter
            itemAdapter.submitList(orderItem.getListItemsData());

            String totalPriceText = binding.getRoot().getContext()
                    .getString(R.string.total_price_le, orderItem.getTotalPrice());
            binding.orderTotalPrice.setText(totalPriceText);

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvOrdersItemsBinding binding = ItemRvOrdersItemsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(itemList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
