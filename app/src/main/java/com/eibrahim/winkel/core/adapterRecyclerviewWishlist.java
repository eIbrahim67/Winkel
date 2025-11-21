package com.eibrahim.winkel.core;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.item.ItemDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class adapterRecyclerviewWishlist extends RecyclerView.Adapter<adapterRecyclerviewWishlist.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewMyItem> itemList;

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getUid();
    private DocumentReference wishlistRef;

    public adapterRecyclerviewWishlist(Context context, List<DataRecyclerviewMyItem> itemList) {
        this.context = context;
        this.itemList = itemList;

        if (userId != null) {
            wishlistRef = firestore.collection("UsersData").document(userId).collection("Wishlist").document("Wishlist");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImage, btnLoveH;
        final TextView itemName, itemCategory, itemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemCategory = itemView.findViewById(R.id.item_cate);
            itemPrice = itemView.findViewById(R.id.item_price);
            btnLoveH = itemView.findViewById(R.id.btn_loveH);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_products, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerviewMyItem item = itemList.get(position);

        holder.itemName.setText(item.getName());
        holder.itemCategory.setText(item.getCategory());
        holder.itemPrice.setText(String.format("%s%s", item.getPrice(), context.getString(R.string.le)));

        Glide.with(context).load(item.getImageId()).into(holder.itemImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item", item);
            context.startActivity(intent);
        });

        // Set icon based on the loved state
        holder.btnLoveH.setImageResource(item.getItemLoved() ? R.drawable.loved_icon : R.drawable.unlove_icon_white);

        holder.btnLoveH.setOnClickListener(v -> {

            if (userId == null) {
                Toast.makeText(context, R.string.you_must_login_first, Toast.LENGTH_SHORT).show();
                return;
            }

            String value = item.getItemId() + "," + item.getItemType();

            boolean isLoved = item.getItemLoved();
            item.setItemLoved(!isLoved);

            // Update UI instantly
            holder.btnLoveH.setImageResource(item.getItemLoved() ? R.drawable.loved_icon : R.drawable.unlove_icon_white);

            // Update Firestore
            wishlistRef.update("Wishlist", item.getItemLoved() ? FieldValue.arrayUnion(value) : FieldValue.arrayRemove(value)).addOnSuccessListener(unused -> {

                if (isLoved) {
                    notifyItemRemoved(position);
                    itemList.remove(item);
                }

            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                // rollback UI on failure
                item.setItemLoved(isLoved);
                holder.btnLoveH.setImageResource(isLoved ? R.drawable.loved_icon : R.drawable.unlove_icon_white);
            });
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
