package com.saveetha.orderly_book.api;

public class ProductRequest {
    private String role;
    private int user_id;
    public ProductRequest(String role, int user_id) {
        this.role = role;
        this.user_id = user_id;
    }
}
