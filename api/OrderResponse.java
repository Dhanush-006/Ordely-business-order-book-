// OrderResponse.java
package com.saveetha.orderly_book.api;

public class OrderResponse {
    private String status;
    private String message;
    private double total_price;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public double getTotal_price() { return total_price; }
}
