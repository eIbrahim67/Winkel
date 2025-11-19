package com.eibrahim.winkel.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.core.DataReviewItem;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AdapterRecyclerviewReviews extends RecyclerView.Adapter<AdapterRecyclerviewReviews.ViewHolder> {

    private final Context context;
    private final List<DataReviewItem> itemList;


    public AdapterRecyclerviewReviews(Context context, List<DataReviewItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView review_name, review_username, review_rate, review_text;
        final ShapeableImageView review_logo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            review_logo = itemView.findViewById(R.id.review_logo);
            review_name = itemView.findViewById(R.id.review_name);
            review_username = itemView.findViewById(R.id.review_username);
            review_rate = itemView.findViewById(R.id.review_rate);
            review_text = itemView.findViewById(R.id.review_text);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataReviewItem currentItem = itemList.get(position);

        Glide.with(context)
                        .load(currentItem.getLogo())
                                .into(holder.review_logo);

        holder.review_name.setText(currentItem.getName());
        holder.review_username.setText(currentItem.getUsername());
        holder.review_rate.setText(currentItem.getRate());
        holder.review_text.setText(currentItem.getReview());
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
