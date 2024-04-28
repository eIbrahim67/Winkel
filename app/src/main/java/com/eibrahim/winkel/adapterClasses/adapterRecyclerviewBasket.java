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

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewMyItem;
import com.eibrahim.winkel.mainPages.CheckoutFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewBasket extends RecyclerView.Adapter<adapterRecyclerviewBasket.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewMyItem> itemList;
    private final CheckoutFragment checkoutFragment;

    public adapterRecyclerviewBasket(Context context, List<DataRecyclerviewMyItem> itemList, CheckoutFragment checkoutActivity) {
        this.context = context;
        this.itemList = itemList;
        this.checkoutFragment = checkoutActivity;
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
        DataRecyclerviewMyItem currentItem = itemList.get(position);

        Glide.with(context)
                .load(currentItem.getImageId())
                .into(holder.itemImage);

        holder.itemNameCheck.setText(currentItem.getName());
        String temp = currentItem.getPrice() + context.getString(R.string.le) ;
        holder.itemPriceCheck.setText(temp);
        holder.itemCateCheck.setText(currentItem.getCategory());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference basketRef = firestore.collection("UsersData")
                .document(userId)
                .collection("BasketCollection")
                .document("BasketDocument");


        holder.btn_sub.setOnClickListener(v -> {

            if (!currentItem.getMuch().equals("1")){

                basketRef
                        .update("BasketCollection", FieldValue.arrayRemove(
                                        currentItem.getItemId() + "," +
                                                currentItem.getItemType() + "," +
                                                currentItem.getMuch() + "," +
                                                currentItem.getItemSize()
                                )
                        )
                        .addOnFailureListener(e -> Toast.makeText(context, "An unexpected error occurred, please refresh the page", Toast.LENGTH_SHORT).show());


                currentItem.setMuch(String.valueOf(Integer.parseInt(currentItem.getMuch()) - 1));
                holder.itemMuchCounter.setText(currentItem.getMuch());

                basketRef
                        .update("BasketCollection", FieldValue.arrayUnion(
                                        currentItem.getItemId() + "," +
                                                currentItem.getItemType() + "," +
                                                currentItem.getMuch() + "," +
                                                currentItem.getItemSize()
                                )
                        )
                        .addOnFailureListener(e -> Toast.makeText(context, "An unexpected error occurred, please refresh the page", Toast.LENGTH_SHORT).show());


            }

        });

        holder.btn_plus.setOnClickListener(v -> {

            basketRef
                    .update("BasketCollection", FieldValue.arrayRemove(
                                    currentItem.getItemId() + "," +
                                            currentItem.getItemType() + "," +
                                            currentItem.getMuch() + "," +
                                            currentItem.getItemSize()
                            )
                    )
                    .addOnFailureListener(e -> Toast.makeText(context, "An unexpected error occurred, please refresh the page", Toast.LENGTH_SHORT).show());


            currentItem.setMuch(String.valueOf(Integer.parseInt(currentItem.getMuch()) + 1));
            holder.itemMuchCounter.setText(currentItem.getMuch());

            basketRef
                    .update("BasketCollection", FieldValue.arrayUnion(
                                    currentItem.getItemId() + "," +
                                            currentItem.getItemType() + "," +
                                            currentItem.getMuch() + "," +
                                            currentItem.getItemSize()
                            )
                    )
                    .addOnFailureListener(e -> Toast.makeText(context, "An unexpected error occurred, please refresh the page", Toast.LENGTH_SHORT).show());


        });

        holder.itemDeleteCheck.setOnClickListener(v -> {

            Toast.makeText(context, "Item deleted from your basket", Toast.LENGTH_SHORT).show();
            if (checkoutFragment != null) {
                checkoutFragment.re(Double.valueOf(currentItem.getTotalPriceItem()));
            }

            basketRef
                    .update("BasketCollection", FieldValue.arrayRemove(
                            currentItem.getItemId() + "," +
                                    currentItem.getItemType() + "," +
                                    currentItem.getMuch() + "," +
                                    currentItem.getItemSize()
                            )
                    )
                    .addOnSuccessListener(unused -> Toast.makeText(context, "Item successfully removed from your basket.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show());

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

