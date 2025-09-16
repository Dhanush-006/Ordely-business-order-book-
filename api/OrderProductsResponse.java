package com.saveetha.orderly_book.api;

import java.util.List;

public class OrderProductsResponse {

    private String status;
    private List<OrderProduct> products;

    public OrderProductsResponse(String status, List<OrderProduct> products) {
        this.status = status;
        this.products = products;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderProduct> getProducts() {
        return products;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProducts(List<OrderProduct> products) {
        this.products = products;
    }
}
