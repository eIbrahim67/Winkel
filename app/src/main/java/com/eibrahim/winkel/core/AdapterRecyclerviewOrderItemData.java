package com.eibrahim.winkel.core;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.databinding.ItemRvOrdersItemDataBinding;

import java.util.Objects;

public class AdapterRecyclerviewOrderItemData
        extends ListAdapter<DataRecyclerviewItemOrderItemData, AdapterRecyclerviewOrderItemData.ViewHolder> {

    public AdapterRecyclerviewOrderItemData() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<DataRecyclerviewItemOrderItemData> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<DataRecyclerviewItemOrderItemData>() {
                @Override
                public boolean areItemsTheSame(@NonNull DataRecyclerviewItemOrderItemData oldItem, @NonNull DataRecyclerviewItemOrderItemData newItem) {
                    // Compare unique IDs
                    return oldItem.getItemId().equals(newItem.getItemId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull DataRecyclerviewItemOrderItemData oldItem, @NonNull DataRecyclerviewItemOrderItemData newItem) {
                    // Compare all fields
                    return Objects.equals(oldItem, newItem);
                }
            };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRvOrdersItemDataBinding binding;

        public ViewHolder(ItemRvOrdersItemDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DataRecyclerviewItemOrderItemData item) {
            binding.itemId.setText(item.getItemId());
            binding.itemCategory.setText(item.getItemCategory());
            binding.itemSize.setText(item.getItemSize());
            binding.itemQuantity.setText(item.getItemMuch());
            String totalPriceText = binding.getRoot().getContext()
                    .getString(R.string.__le, item.getItemTotalPrice());

            binding.itemTotalPrice.setText(totalPriceText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvOrdersItemDataBinding binding = ItemRvOrdersItemDataBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
