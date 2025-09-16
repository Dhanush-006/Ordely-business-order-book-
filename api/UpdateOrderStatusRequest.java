package com.saveetha.orderly_book.api;

public class UpdateOrderStatusRequest {
    private int order_id;
    private String status;

    public UpdateOrderStatusRequest(int order_id, String status) {
        this.order_id = order_id;
        this.status = status;
    }
}