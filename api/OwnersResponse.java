package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OwnersResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("owners")
    private List<User> owners; // Using User class here

    public String getStatus() {
        return status;
    }

    public List<User> getOwners() {
        return owners;
    }
}
