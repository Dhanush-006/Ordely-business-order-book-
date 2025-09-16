package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("business_name")
    private String businessName; // keep it in case your API sends this

    @SerializedName("email")
    private String email;

    @SerializedName("contact")
    private String contact; // matches DB column

    @SerializedName("role") // make sure this matches your backend JSON
    private String role;

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBusinessName() { return businessName; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }
    public String getRole() { return role; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setEmail(String email) { this.email = email; }
    public void setContact(String contact) { this.contact = contact; }
    public void setRole(String role) { this.role = role; }
}
