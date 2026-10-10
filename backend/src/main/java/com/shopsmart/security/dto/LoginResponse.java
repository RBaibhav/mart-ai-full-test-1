package com.shopsmart.security.dto;

public class LoginResponse {
    private String token;
    private CustomerProfileDto customer;

    public LoginResponse() {}

    public LoginResponse(String token, CustomerProfileDto customer) {
        this.token = token;
        this.customer = customer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CustomerProfileDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerProfileDto customer) {
        this.customer = customer;
    }
}
