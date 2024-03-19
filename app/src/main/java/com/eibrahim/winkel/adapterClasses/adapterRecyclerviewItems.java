package com.eibrahim.winkel.adapterClasses;

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
import com.eibrahim.winkel.secondActivity.ItemDetailActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.eibrahim.winkel.mainActivity.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewItems extends RecyclerView.Adapter<adapterRecyclerviewItems.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewMyItem> itemList;
    private final String cate;
    FirebaseFirestore firestore;
    public adapterRecyclerviewItems(Context context, List<DataRecyclerviewMyItem> itemList, String cate) {
        this.context = context;
        this.itemList = itemList;
        this.cate = cate;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImage;
        final ImageView btnLoveH;
        final TextView itemName;
        final TextView itemCategory;
        final TextView itemPrice;


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
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_home_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerviewMyItem currentItem = itemList.get(position);
        
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String temp = currentItem.getPrice() + " LE";

        holder.itemPrice.setText(temp);
        holder.itemCategory.setText(currentItem.getCategory());
        holder.itemName.setText(currentItem.getName());

        Glide.with(context)
                .load(currentItem.getImageId())
                .into(holder.itemImage);


        if (currentItem.getItemLoved())
            holder.btnLoveH.setImageResource(R.drawable.loved_icon);
        else
            holder.btnLoveH.setImageResource(R.drawable.unlove_icon_white);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item", currentItem);
            context.startActivity(intent);
        });

        DocumentReference wishlistRef = firestore.collection("UsersData")
                .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection("WishlistCollection")
                .document("wishlistDocument");


        holder.btnLoveH.setOnClickListener(v -> {


            if (currentItem.getItemLoved()) {
                holder.btnLoveH.setImageResource(R.drawable.unlove_icon_white);
                currentItem.setItemLoved(false);
                wishlistRef
                        .update("WishlistCollection", FieldValue.arrayRemove(currentItem.getItemId() + "," + currentItem.getItemType()))
                        .addOnSuccessListener(unused -> Toast.makeText(context, "Item successfully removed from your wishlist.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show());

            } else {
                holder.btnLoveH.setImageResource(R.drawable.loved_icon);
                currentItem.setItemLoved(true);
                wishlistRef
                        .update("WishlistCollection", FieldValue.arrayUnion(currentItem.getItemId()  + "," + currentItem.getItemType()))
                                .addOnSuccessListener(unused ->
                                    Toast.makeText(context, "Item added into your Wishlist", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show());
            }


        });


        if (Objects.equals(cate, "Mens")){
            HomeFragment.recyclerViewItemsMens_skeleton.setVisibility(View.GONE);
        }
        else if (Objects.equals(cate, "Womens")){
            HomeFragment.recyclerViewItemsWomen_skeleton.setVisibility(View.GONE);
        }
        else if (Objects.equals(cate, "Kids")){
            HomeFragment.recyclerViewItemsKids_skeleton.setVisibility(View.GONE);
        }
        else if (Objects.equals(cate, "Offers")){
            HomeFragment.recyclerViewItemsOffers_skeleton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
