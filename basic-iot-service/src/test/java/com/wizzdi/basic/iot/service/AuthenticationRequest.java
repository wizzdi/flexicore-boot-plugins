package com.wizzdi.basic.iot.service;

public class AuthenticationRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public <T extends AuthenticationRequest> T setEmail(String email) {
        this.email = email;
        return (T) this;
    }

    public String getPassword() {
        return password;
    }

    public <T extends AuthenticationRequest> T setPassword(String password) {
        this.password = password;
        return (T) this;
    }
}
