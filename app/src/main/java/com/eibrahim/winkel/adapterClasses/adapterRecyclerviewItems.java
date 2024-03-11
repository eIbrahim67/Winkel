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

import com.eibrahim.winkel.secondActivity.ItemDetailActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataRecyclerviewItem;
import com.eibrahim.winkel.mianActivity.HomeFragment;
import com.eibrahim.winkel.mianActivity.WishlistFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adapterRecyclerviewItems extends RecyclerView.Adapter<adapterRecyclerviewItems.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewItem> itemList;
    private final String cate;

    private List<String> wishlistIds = new ArrayList<>();

    public WishlistFragment wishlistFragment = new WishlistFragment();
    FirebaseFirestore firestore;
    public adapterRecyclerviewItems(Context context, List<DataRecyclerviewItem> itemList, int type, String cate) {
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
        DataRecyclerviewItem currentItem = itemList.get(position);

        wishlistIds = WishlistFragment.wishlistIds;
        if (wishlistIds.isEmpty())
            wishlistIds = HomeFragment.wishlistIds;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        holder.itemPrice.setText("$" + currentItem.getPrice());
        holder.itemCategory.setText(currentItem.getCategory());
        holder.itemName.setText(currentItem.getName());
        Picasso.with(context)
                .load(currentItem.getImageId())
                .into(holder.itemImage);

        if (wishlistIds.contains(currentItem.getItemId()))
            holder.btnLoveH.setImageResource(R.drawable.love_icon_light);

        else
            holder.btnLoveH.setImageResource(R.drawable.unlove_icon_white);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item", currentItem);
            context.startActivity(intent);
        });

        DocumentReference wishlistRef = firestore.collection("UsersData")
                .document(auth.getCurrentUser().getUid())
                .collection("WishlistCollection")
                .document("wishlistDocument");

        wishlistRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> wishlistData = new HashMap<>();
                    wishlistRef.set(wishlistData)
                            .addOnSuccessListener(aVoid -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                }
            } else {
                // Handle failure
            }
        });

        holder.btnLoveH.setOnClickListener(v -> {

            firestore = FirebaseFirestore.getInstance();

            if (wishlistIds.contains(currentItem.getItemId())) {
                holder.btnLoveH.setImageResource(R.drawable.unlove_icon_white);

                wishlistRef
                        .update("WishlistCollection", FieldValue.arrayRemove(currentItem.getItemId()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Item removed from your Wishlist", Toast.LENGTH_SHORT).show();
                                        wishlistIds.remove(currentItem.getItemId());
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

            } else {
                holder.btnLoveH.setImageResource(R.drawable.love_icon_light);

                wishlistRef
                        .update("WishlistCollection", FieldValue.arrayUnion(currentItem.getItemId()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Item added into your Wishlist", Toast.LENGTH_SHORT).show();
                                        wishlistIds.add(currentItem.getItemId());
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
            }
        });


        if (cate == "Mens"){
            HomeFragment.recyclerViewItemsMens_skeleton.setVisibility(View.GONE);
        }
        else if (cate == "Womens"){
            HomeFragment.recyclerViewItemsWomens_skeleton.setVisibility(View.GONE);
        }
        else if (cate == "Kids"){
            HomeFragment.recyclerViewItemsKids_skeleton.setVisibility(View.GONE);
        }
        else if (cate == "Offers"){
            HomeFragment.recyclerViewItemsOffers_skeleton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
