package com.wizzdi.maps.service.email.response;

public class SendStatusEmailResponse {

    private boolean sent;

    public boolean isSent() {
        return sent;
    }

    public <T extends SendStatusEmailResponse> T setSent(boolean sent) {
        this.sent = sent;
        return (T) this;
    }
}
