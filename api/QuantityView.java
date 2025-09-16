package com.saveetha.orderly_book.api;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.saveetha.orderly_book.R;

public class QuantityView {
    private Product product;
    private int quantity = 0;
    private View view;
    private Runnable onQuantityChangeListener;

    public QuantityView(Context context, Product product) {
        this.product = product;
        view = LayoutInflater.from(context).inflate(R.layout.item_product_quantity, null, false);

        TextView tvName = view.findViewById(R.id.tvProductName);
        TextView tvPrice = view.findViewById(R.id.tvProductPrice);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        Button btnMinus = view.findViewById(R.id.btnMinus);

        // Safety checks
        if (tvName != null) tvName.setText(product.getName());
        if (tvPrice != null) tvPrice.setText("₹" + product.getPrice());
        if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));

        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                quantity++;
                if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
                if (onQuantityChangeListener != null) onQuantityChangeListener.run();
            });
        }

        if (btnMinus != null) {
            btnMinus.setOnClickListener(v -> {
                if (quantity > 0) quantity--;
                if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
                if (onQuantityChangeListener != null) onQuantityChangeListener.run();
            });
        }
    }

    public View getView() {
        return view;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setOnQuantityChangeListener(Runnable listener) {
        this.onQuantityChangeListener = listener;
    }
}
