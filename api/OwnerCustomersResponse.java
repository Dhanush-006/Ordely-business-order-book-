package com.saveetha.orderly_book.api;

import java.util.List;

public class OwnerCustomersResponse {
    private String status;
    private List<Customer> customers;

    public String getStatus() { return status; }
    public List<Customer> getCustomers() { return customers; }

    public void setStatus(String status) { this.status = status; }
    public void setCustomers(List<Customer> customers) { this.customers = customers; }
}
