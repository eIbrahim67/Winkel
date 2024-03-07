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
import com.eibrahim.winkel.declaredClasses.AddToWishlist;
import com.eibrahim.winkel.mianActivity.HomeFragment;
import com.eibrahim.winkel.mianActivity.WishlistFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class adapterRecyclerviewItems extends RecyclerView.Adapter<adapterRecyclerviewItems.ViewHolder> {

    private final Context context;
    private final List<DataRecyclerviewItem> itemList;

    private final int type;

    private List<String> wishlistIds = new ArrayList<>();

    public WishlistFragment wishlistFragment = new WishlistFragment();
    public adapterRecyclerviewItems(Context context, List<DataRecyclerviewItem> itemList, int type) {
        this.context = context;
        this.itemList = itemList;
        this.type = type;
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
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        holder.itemPrice.setText("$" + currentItem.getPrice());
        holder.itemCategory.setText(currentItem.getCategory());
        holder.itemName.setText(currentItem.getName());
        Picasso.with(context)
                .load(currentItem.getImageId()).into(holder.itemImage);

        if (wishlistIds.contains(currentItem.getItemId()))
            holder.btnLoveH.setImageResource(R.drawable.love_icon_light);

        else
            holder.btnLoveH.setImageResource(R.drawable.unlove_icon_white);

        holder.itemImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item", currentItem);
            context.startActivity(intent);
        });


        holder.btnLoveH.setOnClickListener(v -> {
            CollectionReference aaCollection = firestore.collection("UsersData")
                    .document(auth.getCurrentUser().getUid())
                    .collection("WishlistCollection");

            aaCollection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (wishlistIds.contains(currentItem.getItemId())) {
                            holder.btnLoveH.setImageResource(R.drawable.unlove_icon_white);
                            wishlistFragment.checkEmptyWishlist(-1);
                            DocumentReference docRef = firestore.collection("UsersData")
                                    .document(auth.getCurrentUser().getUid())
                                    .collection("WishlistCollection")
                                    .document(currentItem.getItemId());

                            // Handle exceptions (e.g., logging, throwing a custom exception, etc.)
                            docRef.delete()
                                    .addOnSuccessListener(aVoid ->
                                            System.out.println("Document with ID " + currentItem.getItemId() + " successfully deleted."))
                                    .addOnFailureListener(Throwable::printStackTrace);
                            wishlistIds.remove(currentItem.getItemId());
                        } else {
                            holder.btnLoveH.setImageResource(R.drawable.love_icon_light);
                            AddToWishlist addToWishlist = new AddToWishlist();
                            addToWishlist.addItemToBasket(currentItem);
                            Toast.makeText(context, "Item added into your Wishlist", Toast.LENGTH_SHORT).show();
                            wishlistIds.add(currentItem.getItemId());
                        }

                    })
                    .addOnFailureListener(e -> {
                    });
            int adapterPosition = holder.getAdapterPosition();

            if (adapterPosition != RecyclerView.NO_POSITION && type == 2) {
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
