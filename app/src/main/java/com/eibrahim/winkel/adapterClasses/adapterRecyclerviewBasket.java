package com.eibrahim.winkel.adapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.mainActivity.CheckoutFragment;
import com.eibrahim.winkel.mainActivity.WishlistFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewBasket extends RecyclerView.Adapter<adapterRecyclerviewBasket.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewItem> itemList;
    private final CheckoutFragment checkoutFragment;

    public adapterRecyclerviewBasket(Context context, List<DataRecyclerviewItem> itemList, CheckoutFragment checkoutActivity) {
        this.context = context;
        this.itemList = itemList;
        this.checkoutFragment = checkoutActivity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImage;
        final ImageView itemDeleteCheck;

        final TextView itemNameCheck;
        final TextView itemPriceCheck;
        final TextView itemMuchCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImgCheck);
            itemNameCheck = itemView.findViewById(R.id.itemNameCheck);
            itemPriceCheck = itemView.findViewById(R.id.itemPriceCheck);
            itemMuchCheck = itemView.findViewById(R.id.itemMuchCheck);
            itemDeleteCheck = itemView.findViewById(R.id.itemDeleteCheck);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_basket_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRecyclerviewItem currentItem = itemList.get(position);

        Picasso.with(context).load(currentItem.getImageId()).into(holder.itemImage);
        holder.itemNameCheck.setText(currentItem.getName());
        String temp = "$" + currentItem.getPrice();
        holder.itemPriceCheck.setText(temp);
        temp = "x" + currentItem.getMuch();
        holder.itemMuchCheck.setText(temp);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        holder.itemDeleteCheck.setOnClickListener(v -> {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
            if (checkoutFragment != null) {
                checkoutFragment.re(Double.valueOf(currentItem.getTotalPriceItem()));
            }

            DocumentReference basketRef = firestore.collection("UsersData")
                    .document(userId)
                    .collection("BasketCollection")
                    .document("BasketDocument");

            basketRef
                    .update("BasketCollection", FieldValue.arrayRemove(currentItem.getItemId()  + "," + currentItem.getItemType() + "," + currentItem.getMuch()))
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Item removed from your Basket", Toast.LENGTH_SHORT).show();
                        WishlistFragment.wishlistIds.add(currentItem.getItemId());
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "unExpected error", Toast.LENGTH_SHORT).show());

            int adapterPosition = holder.getAdapterPosition();

            if (adapterPosition != RecyclerView.NO_POSITION) {
                itemList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}

