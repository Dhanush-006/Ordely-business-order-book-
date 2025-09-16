package com.saveetha.orderly_book.api;

public class OrderProduct {

    private int id;
    private String name;
    private String price;
    private int quantity;

    public OrderProduct(int id, String name, String price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
