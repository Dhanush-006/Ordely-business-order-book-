package com.saveetha.orderly_book.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.api.OrderDetailsResponse;
import com.saveetha.orderly_book.R;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {

    private List<OrderDetailsResponse.OrderItem> items;

    public OrderDetailsAdapter(List<OrderDetailsResponse.OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailsResponse.OrderItem item = items.get(position);
        holder.tvProductName.setText(item.getProductName());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());
        holder.tvPrice.setText("Price: ₹ " + item.getProductPrice());
        holder.tvLineTotal.setText("Subtotal: ₹ " + item.getLineTotal());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice, tvLineTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvLineTotal = itemView.findViewById(R.id.tvLineTotal);
        }
    }
}
