package com.saveetha.orderly_book;

import com.saveetha.orderly_book.api.OrderRequest;

import java.util.List;

public class PaymentDummyData {
    private static List<OrderRequest.ProductOrder> selectedProducts;

    public static void setSelectedProducts(List<OrderRequest.ProductOrder> products) {
        selectedProducts = products;
    }

    public static List<OrderRequest.ProductOrder> getSelectedProducts() {
        return selectedProducts;
    }
}
