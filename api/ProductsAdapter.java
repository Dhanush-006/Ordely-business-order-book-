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

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final List<Integer> quantities = new ArrayList<>();
    private OnQuantityChangeListener quantityChangeListener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    public ProductsAdapter(List<Product> productList) {
        this.productList = productList != null ? productList : new ArrayList<>();
        for (int i = 0; i < this.productList.size(); i++) quantities.add(0);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_quantity, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("₹ " + product.getPrice());
        holder.tvQuantity.setText(String.valueOf(quantities.get(position)));

        // Increase quantity
        holder.btnPlus.setOnClickListener(v -> {
            int qty = quantities.get(position) + 1;
            quantities.set(position, qty);
            holder.tvQuantity.setText(String.valueOf(qty));
            if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
        });

        // Decrease quantity
        holder.btnMinus.setOnClickListener(v -> {
            int qty = quantities.get(position);
            if (qty > 0) {
                qty--;
                quantities.set(position, qty);
                holder.tvQuantity.setText(String.valueOf(qty));
                if (quantityChangeListener != null) quantityChangeListener.onQuantityChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /** Calculate total price */
    public double getTotal() {
        double total = 0;
        for (int i = 0; i < productList.size(); i++) {
            double price = 0;
            try {
                price = Double.parseDouble(productList.get(i).getPrice());
            } catch (NumberFormatException ignored) {}
            total += price * quantities.get(i);
        }
        return total;
    }

    /** Get selected products with quantity > 0 */
    public List<OrderRequest.ProductOrder> getSelectedProducts() {
        List<OrderRequest.ProductOrder> selected = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            int qty = quantities.get(i);
            if (qty > 0) {
                selected.add(new OrderRequest.ProductOrder(productList.get(i).getId(), qty));
            }
        }
        return selected;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        Button btnPlus, btnMinus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
