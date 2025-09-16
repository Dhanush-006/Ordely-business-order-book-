package com.saveetha.orderly_book.api;

import java.util.List;

public class    OwnerOrder {
    private int id;
    private int customer_id;
    private String customer_name;
    private double total_price;
    private String status;
    private List<OrderProduct> products; // list of products for this order

    public int getId() { return id; }
    public int getCustomer_id() { return customer_id; }
    public String getCustomer_name() { return customer_name; }
    public double getTotal_price() { return total_price; }
    public String getStatus() { return status; }
    public List<OrderProduct> getProducts() { return products; }

    public static class OrderProduct {
        private String name;
        private double price;
        private int quantity;

        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
    }
}
