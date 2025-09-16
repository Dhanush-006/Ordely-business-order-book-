package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CustomerOrdersResponse {

    @SerializedName("status")
    private String status; // "success" or "error"

    @SerializedName("monthly_totals")
    private List<Float> monthlyTotals; // total purchase amount for each month (Jan-Dec)

    // Optional: if you want to include the orders themselves
    @SerializedName("orders")
    private List<Order> orders;

    // ---------------- Getters ----------------
    public String getStatus() {
        return status;
    }

    public List<Float> getMonthlyTotals() {
        return monthlyTotals;
    }

    public List<Order> getOrders() {
        return orders;
    }

    // ---------------- Setters ----------------
    public void setStatus(String status) {
        this.status = status;
    }

    public void setMonthlyTotals(List<Float> monthlyTotals) {
        this.monthlyTotals = monthlyTotals;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
