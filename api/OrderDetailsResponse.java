package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDetailsResponse {

    private String status;

    @SerializedName("order_id")
    private int orderId;

    private double total_price;

    private List<OrderItem> items;

    public String getStatus() { return status; }
    public int getOrderId() { return orderId; }
    public double getTotal_price() { return total_price; }
    public List<OrderItem> getItems() { return items; }

    public static class OrderItem {
        @SerializedName("product_name")
        private String productName;

        private int quantity;

        @SerializedName("product_price")
        private double productPrice;

        @SerializedName("line_total")
        private double lineTotal;

        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getProductPrice() { return productPrice; }
        public double getLineTotal() { return lineTotal; }
    }
}
