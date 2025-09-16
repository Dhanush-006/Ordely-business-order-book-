package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("owner_name")   // ✅ new field for customer view
    private String ownerName;

    @SerializedName("total_price")
    private double totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("order_date")
    private String orderDate;

    public Order() {}

    // Getters
    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getOwnerName() { return ownerName; }  // ✅ added
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getOrderDate() { return orderDate; }

    // Setters
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }  // ✅ added
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
}
