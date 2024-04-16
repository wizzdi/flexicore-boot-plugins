package com.wizzdi.alerts.app;

public class AuthenticationResponse {

    private String authenticationKey;

    public String getAuthenticationKey() {
        return authenticationKey;
    }

    public <T extends AuthenticationResponse> T setAuthenticationKey(String authenticationKey) {
        this.authenticationKey = authenticationKey;
        return (T) this;
    }
}
