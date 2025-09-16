package com.saveetha.orderly_book.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveetha.orderly_book.R;

import java.util.List;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.ProductViewHolder> {

    private final List<OrderProduct> products;

    public OrderProductsAdapter(List<OrderProduct> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        OrderProduct product = products.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvQuantity.setText("Qty: " + product.getQuantity());
        holder.tvPrice.setText("Price: ₹" + product.getPrice());

        double total = 0;
        try {
            total = Double.parseDouble(product.getPrice()) * product.getQuantity();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        holder.tvTotal.setText("Total: ₹" + total);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice, tvTotal;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
