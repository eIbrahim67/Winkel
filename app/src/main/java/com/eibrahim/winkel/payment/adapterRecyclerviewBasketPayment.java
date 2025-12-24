package com.eibrahim.winkel.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataRecyclerviewMyItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewBasketPayment
        extends RecyclerView.Adapter<adapterRecyclerviewBasketPayment.ViewHolder> {

    private final List<DataRecyclerviewMyItem> itemList;
    private final PaymentActivity paymentActivity;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final String userId =
            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    private Context context;

    public adapterRecyclerviewBasketPayment(
            List<DataRecyclerviewMyItem> itemList,
            PaymentActivity paymentActivity) {

        this.itemList = itemList;
        this.paymentActivity = paymentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_basket_items, parent, false);
        context = parent.getContext();
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

        updateMinusIcon(holder, Integer.parseInt(item.getMuch()));

        // MINUS
        holder.btn_sub.setOnClickListener(v -> {
            int qty = Integer.parseInt(item.getMuch());

            if (qty <= 1) {
                deleteItem(holder.getAdapterPosition(), item, holder.itemView);
                return;
            }

            updateQuantity(item, -1, holder);
        });

        // PLUS
        holder.btn_plus.setOnClickListener(v ->
                updateQuantity(item, +1, holder)
        );

        // DELETE
        holder.itemDeleteCheck.setOnClickListener(v ->
                deleteItem(holder.getAdapterPosition(), item, holder.itemView)
        );
    }

    // --------------------------
    // UPDATE QUANTITY
    // --------------------------
    private void updateQuantity(
            DataRecyclerviewMyItem item,
            int diff,
            ViewHolder holder) {

        int currentQty = Integer.parseInt(item.getMuch());
        int newQty = currentQty + diff;

        if (newQty < 1) return;

        double price = Double.parseDouble(item.getPrice());

        // UI update first (optimistic)
        item.setMuch(String.valueOf(newQty));
        holder.itemMuchCounter.setText(item.getMuch());
        updateMinusIcon(holder, newQty);

        if (paymentActivity != null) {
            paymentActivity.updateAfterChange(price, diff > 0 ? '+' : '-');
        }

        updateFirestoreQuantity(item, diff, holder.itemView);
    }

    // --------------------------
    // FIRESTORE UPDATE
    // --------------------------
    private void updateFirestoreQuantity(
            DataRecyclerviewMyItem item,
            int diff,
            View view) {

        String docId = item.getItemId() + "_" + item.getItemSize();

        DocumentReference ref = firestore.collection("UsersData")
                .document(userId)
                .collection("Basket")
                .document(docId);

        firestore.runTransaction(transaction -> {

            DocumentSnapshot snap = transaction.get(ref);
            if (!snap.exists()) return null;

            long currentQty = snap.getLong("quantity");
            long newQty = currentQty + diff;

            if (newQty > 0) {
                transaction.update(ref, "quantity", newQty);
            } else {
                transaction.delete(ref);
            }

            return null;

        }).addOnFailureListener(e ->
                Snackbar.make(view,
                        R.string.error_updating_basket,
                        Snackbar.LENGTH_SHORT).show()
        );
    }

    // --------------------------
    // DELETE ITEM
    // --------------------------
    private void deleteItem(
            int position,
            DataRecyclerviewMyItem item,
            View view) {

        if (position == RecyclerView.NO_POSITION) return;

        String docId = item.getItemId() + "_" + item.getItemSize();

        firestore.collection("UsersData")
                .document(userId)
                .collection("Basket")
                .document(docId)
                .delete();

        if (paymentActivity != null) {
            paymentActivity.removeItem(
                    item.getTotalPriceItem(),
                    docId
            );
        }

        itemList.remove(position);
        notifyItemRemoved(position);

        Snackbar.make(view,
                R.string.item_removed,
                Snackbar.LENGTH_SHORT).show();
    }

    private void updateMinusIcon(ViewHolder holder, int qty) {
        int icon = qty > 1
                ? R.drawable.sub_icon_black
                : R.drawable.trash_icon_black;

        Glide.with(holder.itemView)
                .load(icon)
                .into(holder.btn_sub);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // --------------------------
    // VIEW HOLDER
    // --------------------------
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage, itemDeleteCheck, btn_sub, btn_plus;
        TextView itemCateCheck, itemNameCheck, itemPriceCheck, itemMuchCounter;

        ViewHolder(@NonNull View itemView) {
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
}
