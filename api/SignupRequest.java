package com.saveetha.orderly_book.api;
public class SignupRequest {
    private String name, email, password, role;
    public SignupRequest(String name, String email, String password, String role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}