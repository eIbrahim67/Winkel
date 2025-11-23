package com.eibrahim.winkel.payment;

import android.content.Context;
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
import com.eibrahim.winkel.checkout.CheckoutFragment;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class adapterRecyclerviewBasketPayment extends RecyclerView.Adapter<adapterRecyclerviewBasketPayment.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewMyItem> itemList;
    private final PaymentActivity checkoutFragment;

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public adapterRecyclerviewBasketPayment(Context context, List<DataRecyclerviewMyItem> itemList, PaymentActivity fragment) {
        this.context = context;
        this.itemList = itemList;
        this.checkoutFragment = fragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImage, itemDeleteCheck, btn_sub, btn_plus;
        final TextView itemCateCheck, itemNameCheck, itemPriceCheck, itemMuchCounter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImgCheck);
            itemNameCheck = itemView.findViewById(R.id.itemNameCheck);
            itemPriceCheck = itemView.findViewById(R.id.itemPriceCheck);
            itemDeleteCheck = itemView.findViewById(R.id.itemDeleteCheck);
            itemCateCheck = itemView.findViewById(R.id.itemCateCheck);
            btn_sub = itemView.findViewById(R.id.btn_sub);
            btn_plus = itemView.findViewById(R.id.btn_plus);
            itemMuchCounter = itemView.findViewById(R.id.itemMuchCounter);
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
        DataRecyclerviewMyItem item = itemList.get(position);

        Glide.with(context).load(item.getImageId()).into(holder.itemImage);

        holder.itemNameCheck.setText(item.getName());
        holder.itemPriceCheck.setText(item.getPrice() + context.getString(R.string.le));
        holder.itemCateCheck.setText(item.getCategory());
        holder.itemMuchCounter.setText(item.getMuch());

        // --------------------------
        // QUANTITY - (MINUS BUTTON)
        // --------------------------
        holder.btn_sub.setOnClickListener(v -> {
            int amount = Integer.parseInt(item.getMuch());

            if (amount <= 1) return;

            updateQuantity(item, -1, holder);
        });

        // --------------------------
        // QUANTITY + (PLUS BUTTON)
        // --------------------------
        holder.btn_plus.setOnClickListener(v -> {
            updateQuantity(item, +1, holder);
        });

        // --------------------------
        // DELETE ITEM
        // --------------------------
        holder.itemDeleteCheck.setOnClickListener(v -> deleteItem(holder.getAdapterPosition(), item));
    }

    // --------------------------
    // UPDATE QUANTITY
    // --------------------------
    private void updateQuantity(DataRecyclerviewMyItem item, int diff, ViewHolder holder) {

        int newMuch = Integer.parseInt(item.getMuch()) + diff;
        double price = Double.parseDouble(item.getPrice());

        // update UI instantly
        item.setMuch(String.valueOf(newMuch));
        holder.itemMuchCounter.setText(item.getMuch());

        // update totals in fragment
        if (checkoutFragment != null)
            checkoutFragment.updateAfterChange(price, diff > 0 ? '+' : '-');

        // Firestore update
        updateFirestore(item, diff);
    }

    // --------------------------
    // UPDATE FIRESTORE QUANTITIES
    // --------------------------
    private void updateFirestore(DataRecyclerviewMyItem item, int diff) {

        String base = item.getItemId() + "," + item.getItemType() + "," + item.getMuch() + "," + item.getItemSize();

        String oldBase = item.getItemId() + "," + item.getItemType() + "," + (Integer.parseInt(item.getMuch()) - diff) + "," + item.getItemSize();

        firestore.collection("UsersData").document(userId).collection("BasketCollection").document("BasketDocument").update("BasketCollection", FieldValue.arrayRemove(oldBase)).addOnSuccessListener(unused -> firestore.collection("UsersData").document(userId).collection("BasketCollection").document("BasketDocument").update("BasketCollection", FieldValue.arrayUnion(base))).addOnFailureListener(e -> Toast.makeText(context, R.string.error_updating_basket, Toast.LENGTH_SHORT).show());
    }

    // --------------------------
    // DELETE ITEM
    // --------------------------
    private void deleteItem(int position, DataRecyclerviewMyItem item) {

        firestore.collection("UsersData").document(userId).collection("BasketCollection").document("BasketDocument").update("BasketCollection", FieldValue.arrayRemove(item.getItemId() + "," + item.getItemType() + "," + item.getMuch() + "," + item.getItemSize()));

        if (checkoutFragment != null) checkoutFragment.removeItem(item.getTotalPriceItem());

        itemList.remove(position);
        notifyItemRemoved(position);

        Toast.makeText(context, R.string.item_removed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
