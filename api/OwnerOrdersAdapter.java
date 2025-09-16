package com.saveetha.orderly_book.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.R;
import com.saveetha.orderly_book.api.OwnerOrder;

import java.util.List;

public class OwnerOrdersAdapter extends RecyclerView.Adapter<OwnerOrdersAdapter.ViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(OwnerOrder order);
    }

    private List<OwnerOrder> orders;
    private OnOrderClickListener listener;

    public OwnerOrdersAdapter(List<OwnerOrder> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_owner_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OwnerOrder order = orders.get(position);
        holder.tvCustomer.setText(order.getCustomer_name());
        holder.tvTotal.setText("Total: ₹" + order.getTotal_price());
        holder.tvStatus.setText(order.getStatus());

        holder.btnAccept.setOnClickListener(v -> {
            // TODO: API call to accept order
        });
        holder.btnReject.setOnClickListener(v -> {
            // TODO: API call to reject order
        });

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomer, tvTotal, tvStatus;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
