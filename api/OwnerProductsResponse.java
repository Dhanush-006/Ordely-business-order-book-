
package com.saveetha.orderly_book.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OwnerProductsResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("products")
    private List<Product> products;

    public String getStatus() { return status; }
    public List<Product> getProducts() { return products; }
}
