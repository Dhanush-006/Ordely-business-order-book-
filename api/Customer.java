package com.saveetha.orderly_book.api;

public class Customer {
    private int id;
    private String name;

    // Constructor
    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }

    // Setters (if needed)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
