package com.eibrahim.winkel.adapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.DataPaymentMethodItem;

import java.util.List;

public class adapterRecyclerviewPaymentMethods extends RecyclerView.Adapter<adapterRecyclerviewPaymentMethods.ViewHolder> {

    private final Context context;
    private final List<DataPaymentMethodItem> itemList;
    public adapterRecyclerviewPaymentMethods(Context context, List<DataPaymentMethodItem> itemList) {
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
        DataPaymentMethodItem currentItem = itemList.get(position);

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
