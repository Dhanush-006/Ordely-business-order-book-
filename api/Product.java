package com.saveetha.orderly_book.api;

public class Product {
    private int id;
    private int owner_id;
    private String name;
    private String price; // Keep as String to match JSON

    public int getId() { return id; }
    public int getOwnerId() { return owner_id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
}
