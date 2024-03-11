package com.eibrahim.winkel.adapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.declaredClasses.FetchDataFromFirebase;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewFilter extends RecyclerView.Adapter<adapterRecyclerviewFilter.ViewHolder> {

    private final List<String> itemList;
    private final RecyclerView recyclerView_items;
    private final Context context;
    private final String type;
    private final String tPrice;
    private final String fPrice;
    private ViewHolder lastHolder = null;

    public adapterRecyclerviewFilter(List<String> itemList, RecyclerView recyclerView_items, String type, String fPrice, String tPrice, Context context) {
        this.itemList = itemList;
        this.recyclerView_items = recyclerView_items;
        this.context = context;
        this.type = type;
        this.tPrice = tPrice;
        this.fPrice = fPrice;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_home_filters, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String filter = String.valueOf(itemList.get(position));

        holder.itemCategoryType.setText(filter);

        holder.itemCategoryTypeSelected.setText(filter);

        if (lastHolder == null && Objects.equals(filter, "All")){
            holder.itemCategoryTypeSelected.setVisibility(View.VISIBLE);
            lastHolder = holder;
        }

        if (position == (getItemCount() - 1)){
            ((ViewGroup.MarginLayoutParams)holder.itemView.getLayoutParams()).setMarginEnd(0);
            holder.spaceMarginEnd.setVisibility(View.VISIBLE);
        }

        if (position == 0){
            holder.spaceMarginStart.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> holder.itemCategoryType.callOnClick());

        holder.itemCategoryTypeSelected.setOnClickListener(v -> holder.itemCategoryType.callOnClick());

        holder.itemCategoryType.setOnClickListener(v -> {

            FetchDataFromFirebase fetchDataFromFirebase = new FetchDataFromFirebase(
                    recyclerView_items,
                    recyclerView_items,
                    recyclerView_items,
                    recyclerView_items,
                    recyclerView_items,
                    context);

            if (Objects.equals(filter, "All")){
                fetchDataFromFirebase.fetchData(type, fPrice, tPrice, 1, recyclerView_items);
            }else{
                fetchDataFromFirebase.fetchFilterData(filter, type, fPrice, tPrice);
            }

            if (lastHolder != null)
                lastHolder.itemCategoryTypeSelected.setVisibility(View.GONE);

            lastHolder = holder;

            holder.itemCategoryTypeSelected.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView itemCategoryType;

        final TextView itemCategoryTypeSelected;

        final Space spaceMarginStart;
        final Space spaceMarginEnd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCategoryType = itemView.findViewById(R.id.itemCategoryType);
            itemCategoryTypeSelected = itemView.findViewById(R.id.itemCategoryTypeSelected);
            spaceMarginStart = itemView.findViewById(R.id.spaceMarginStart);
            spaceMarginEnd = itemView.findViewById(R.id.spaceMarginEnd);

        }
    }

}
