package com.saveetha.orderly_book.api;

public class LoginResponse {
    private String status;
    private String message;
    private User user;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
}
