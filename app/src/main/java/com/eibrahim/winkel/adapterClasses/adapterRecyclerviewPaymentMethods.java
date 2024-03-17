package com.eibrahim.winkel.adapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.dataRecyclerviewPaymentMethods;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewPaymentMethods extends RecyclerView.Adapter<adapterRecyclerviewPaymentMethods.ViewHolder> {

    private final Context context;
    private final List<dataRecyclerviewPaymentMethods> itemList;
    private ViewHolder lastHolder = null;
    public adapterRecyclerviewPaymentMethods(Context context, List<dataRecyclerviewPaymentMethods> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView holder_name_mastercard, cvv_mastercard, number_mastercard, holder_name_visa, cvv_visa, number_visa ;
        final RelativeLayout visa, mastercard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            holder_name_mastercard = itemView.findViewById(R.id.holder_name_mastercard);
            cvv_mastercard = itemView.findViewById(R.id.cvv_mastercard);
            number_mastercard = itemView.findViewById(R.id.number_mastercard);

            holder_name_visa = itemView.findViewById(R.id.holder_name_visa);
            cvv_visa = itemView.findViewById(R.id.cvv_visa);
            number_visa = itemView.findViewById(R.id.number_visa);

            visa = itemView.findViewById(R.id.visa);
            mastercard = itemView.findViewById(R.id.mastercard);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.credit_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dataRecyclerviewPaymentMethods currentItem = itemList.get(position);

        switch (currentItem.getType()){

            case "visa":
                holder.number_visa.setText(currentItem.getNumber());
                holder.holder_name_visa.setText(currentItem.getHolder_name());
                holder.cvv_visa.setText(currentItem.getCvv());
                holder.visa.setVisibility(View.VISIBLE);
                break;

            case "mastercard":
                holder.number_mastercard.setText(currentItem.getNumber());
                holder.holder_name_mastercard.setText(currentItem.getHolder_name());
                holder.cvv_mastercard.setText(currentItem.getCvv());
                holder.mastercard.setVisibility(View.VISIBLE);
                break;
        }

    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
