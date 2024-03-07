package com.eibrahim.winkel.adapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eibrahim.winkel.secondActivity.PaymentActivity;
import com.eibrahim.winkel.R;
import com.eibrahim.winkel.dataClasses.dataRecyclerviewPaymentMethods;

import java.util.List;
import java.util.Objects;

public class adapterRecyclerviewPaymentMethods extends RecyclerView.Adapter<adapterRecyclerviewPaymentMethods.ViewHolder> {

    private final Context context;
    private final List<dataRecyclerviewPaymentMethods> itemList;
    private ViewHolder lastHolder = null;
    private final PaymentActivity paymentActivity;
    public adapterRecyclerviewPaymentMethods(Context context, List<dataRecyclerviewPaymentMethods> itemList, PaymentActivity paymentActivity) {
        this.context = context;
        this.itemList = itemList;
        this.paymentActivity = paymentActivity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView itemImgPayment;
        final ImageView itemImgPaymentSelected;
        final TextView itemNumPayment;
        final TextView itemNumPaymentSelected;
        final TextView itemDatePayment;
        final TextView itemDatePaymentSelected;
        final LinearLayout itemSelected;
        final LinearLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImgPayment = itemView.findViewById(R.id.itemImgPayment);
            itemNumPayment = itemView.findViewById(R.id.itemNumPayment);
            itemDatePayment = itemView.findViewById(R.id.itemDatePayment);

            itemSelected = itemView.findViewById(R.id.itemSelected);
            item = itemView.findViewById(R.id.item);

            itemImgPaymentSelected = itemView.findViewById(R.id.itemImgPaymentSelected);
            itemNumPaymentSelected = itemView.findViewById(R.id.itemNumPaymentSelected);
            itemDatePaymentSelected = itemView.findViewById(R.id.itemDatePaymentSelected);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_payment_method, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dataRecyclerviewPaymentMethods currentItem = itemList.get(position);

        holder.itemDatePayment.setText(currentItem.getDate());
        holder.itemNumPayment.setText(currentItem.getNumber());


        if (Objects.equals(currentItem.getType(), "master card")){
            holder.itemImgPaymentSelected.setImageResource(R.drawable.mastercard_v1);
            holder.itemImgPayment.setImageResource(R.drawable.mastercard_v2);
        }
        else if (Objects.equals(currentItem.getType(), "visa")){
            holder.itemImgPaymentSelected.setImageResource(R.drawable.visa_v1);
            holder.itemImgPayment.setImageResource(R.drawable.visa_v2);
        }
        else {
            holder.itemImgPaymentSelected.setImageResource(R.drawable.paypal_v1);
            holder.itemImgPayment.setImageResource(R.drawable.paypal_v2);
        }

        holder.itemNumPaymentSelected.setText(currentItem.getNumber());
        holder.itemDatePaymentSelected.setText(currentItem.getDate());

        holder.item.setOnClickListener(v -> holder.itemView.callOnClick());

        holder.itemSelected.setOnClickListener(v -> holder.itemView.callOnClick());

        holder.itemView.setOnClickListener(v -> {
            paymentActivity.donePayment(true);
            if(holder.itemSelected.getVisibility() == View.GONE){
                holder.itemSelected.setVisibility(View.VISIBLE);
                if (lastHolder != null)
                    lastHolder.itemSelected.setVisibility(View.GONE);
                lastHolder = holder;
            }
        });
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
