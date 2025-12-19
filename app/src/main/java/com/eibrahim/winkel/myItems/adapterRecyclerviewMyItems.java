package com.eibrahim.winkel.myItems;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.eibrahim.winkel.item.ItemDetailActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class adapterRecyclerviewMyItems extends RecyclerView.Adapter<adapterRecyclerviewMyItems.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewMyItem> itemList;
    FirebaseFirestore firestore;

    public adapterRecyclerviewMyItems(Context context, List<DataRecyclerviewMyItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImage;
        final ImageView btnDelete;
        final TextView itemName;
        final TextView itemCategory;
        final TextView itemPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.my_item_image);
            itemName = itemView.findViewById(R.id.my_item_name);
            itemCategory = itemView.findViewById(R.id.my_item_cate);
            itemPrice = itemView.findViewById(R.id.my_item_price);
            btnDelete = itemView.findViewById(R.id.btn_delete);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerviewMyItem currentItem = itemList.get(position);

        firestore = FirebaseFirestore.getInstance();

        String temp = currentItem.getPrice() + context.getString(R.string.le);

        holder.itemPrice.setText(temp);
        holder.itemCategory.setText(currentItem.getCategory());
        holder.itemName.setText(currentItem.getName());

        Glide.with(context)
                .load(currentItem.getImageId())
                .into(holder.itemImage);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item", currentItem);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {

            String itemId = currentItem.getItemId();
            String itemType = currentItem.getItemType();

            new AlertDialog.Builder(context)
                    .setTitle(R.string.confirm_deletion)
                    .setMessage(context.getText(R.string.are_you_sure_you_want_to_permanently_remove_this_item_from_your_shop))
                    .setPositiveButton(context.getText(R.string.delete), (dialog, which) -> {
                        dialog.dismiss();

                        FirebaseFirestore.getInstance()
                                .collection("Products")
                                .document(itemType)
                                .collection(itemType)
                                .document(itemId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Snackbar.make(holder.itemView, context.getText(R.string.item_successfully_deleted), Snackbar.LENGTH_SHORT).show();
                                    itemList.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                })
                                .addOnFailureListener(e -> Snackbar.make(holder.itemView, R.string.failed_to_delete_the_item_please_try_again, Snackbar.LENGTH_SHORT).show());
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
