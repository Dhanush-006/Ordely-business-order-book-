package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;

public class Owner {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("contact")
    private String contact; // Add this field

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact; // Add this getter
    }
}
