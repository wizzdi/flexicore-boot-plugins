package com.wizzdi.video.conference.service.response;

import java.time.OffsetDateTime;

public class JitsiTokenResponse {
    private String token;
    private OffsetDateTime validTill;

    public String getToken() {
        return token;
    }

    public <T extends JitsiTokenResponse> T setToken(String token) {
        this.token = token;
        return (T) this;
    }

    public OffsetDateTime getValidTill() {
        return validTill;
    }

    public <T extends JitsiTokenResponse> T setValidTill(OffsetDateTime validTill) {
        this.validTill = validTill;
        return (T) this;
    }
}
