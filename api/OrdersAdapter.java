package com.saveetha.orderly_book.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final OnOrderActionListener listener;
    private final boolean isBusinessOrder; // true = Owner/Business, false = Customer

    // Constructor
    public OrdersAdapter(List<Order> orders, OnOrderActionListener listener, boolean isBusinessOrder) {
        this.orders = new ArrayList<>(orders);
        this.listener = listener;
        this.isBusinessOrder = isBusinessOrder;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using same layout for now
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvTotalPrice.setText("₹ " + order.getTotalPrice());
        holder.tvStatus.setText("Status: " + (order.getStatus() != null ? order.getStatus() : "pending"));

        if (isBusinessOrder) {
            // Owner/Business view
            holder.tvCustomerName.setText("Customer: " +
                    (order.getCustomerName() != null ? order.getCustomerName() : "Unknown"));
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);

            holder.btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(order);
            });

            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(order);
            });

        } else {
            // Customer view
            holder.tvCustomerName.setText("Owner: " +
                    (order.getOwnerName() != null ? order.getOwnerName() : "Unknown"));
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        // Item click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    /** Update list for search/filter */
    public void updateList(List<Order> filteredOrders) {
        orders.clear();
        orders.addAll(filteredOrders);
        notifyDataSetChanged();
    }

    /** ViewHolder */
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvTotalPrice, tvStatus;
        Button btnAccept, btnReject;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }

    /** Listener interface */
    public interface OnOrderActionListener {
        void onAccept(Order order);
        void onReject(Order order);
        void onOrderClick(Order order);
    }
}
