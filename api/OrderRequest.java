package com.saveetha.orderly_book.api;

import java.util.List;

public class OrderRequest {
    private int customer_id;
    private List<ProductOrder> products;

    public OrderRequest(int customer_id, List<ProductOrder> products) {
        this.customer_id = customer_id;
        this.products = products;
    }

    public static class ProductOrder {
        private int product_id;
        private int quantity;

        public ProductOrder(int product_id, int quantity) {
            this.product_id = product_id;
            this.quantity = quantity;
        }
    }
}
