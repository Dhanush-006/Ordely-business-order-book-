package com.saveetha.orderly_book.api;

import java.util.List;

public class ProductResponse {
    private String status;
    private List<Product> products;

    public String getStatus() { return status; }
    public List<Product> getProducts() { return products; }
}
