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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private final OnProductClickListener listener;
    private final boolean canOrder; // ✅ determines if Order button is visible

    // Listener interface
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Constructor
    public ProductAdapter(List<Product> products, OnProductClickListener listener, boolean canOrder) {
        this.products = products != null ? products : new ArrayList<>();
        this.listener = listener;
        this.canOrder = canOrder;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("₹ " + product.getPrice());

        // Show or hide Order button based on canOrder
        if (canOrder) {
            holder.btnOrder.setVisibility(View.VISIBLE);
            holder.btnOrder.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });
        } else {
            holder.btnOrder.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // Update products dynamically
    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        Button btnOrder;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }
}
