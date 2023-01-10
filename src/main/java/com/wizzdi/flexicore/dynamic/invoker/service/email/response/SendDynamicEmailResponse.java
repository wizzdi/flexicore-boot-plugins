package com.wizzdi.flexicore.dynamic.invoker.service.email.response;

public class SendDynamicEmailResponse {

    private boolean sent;

    public boolean isSent() {
        return sent;
    }

    public <T extends SendDynamicEmailResponse> T setSent(boolean sent) {
        this.sent = sent;
        return (T) this;
    }
}
