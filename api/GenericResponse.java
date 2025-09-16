package com.saveetha.orderly_book.api;

public class GenericResponse {
    private String status;
    private String message;
    private int product_id; // returned only on success

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public int getProductId() { return product_id; }
}
