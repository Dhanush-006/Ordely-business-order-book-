package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrdersResponse {
    private String status;

    @SerializedName("orders")
    private List<Order> orders;

    public String getStatus() {
        return status;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
