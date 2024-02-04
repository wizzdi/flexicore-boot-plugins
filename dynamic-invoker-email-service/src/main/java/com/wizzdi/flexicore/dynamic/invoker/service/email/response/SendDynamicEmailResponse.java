package com.wizzdi.flexicore.dynamic.invoker.service.email.response;

public class SendDynamicEmailResponse {

    private boolean sent;
    private int status;
    private String message;


    public boolean isSent() {
        return sent;
    }

    public <T extends SendDynamicEmailResponse> T setSent(boolean sent) {
        this.sent = sent;
        return (T) this;
    }

    public int getStatus() {
        return status;
    }

    public <T extends SendDynamicEmailResponse> T setStatus(int status) {
        this.status = status;
        return (T) this;
    }

    public String getMessage() {
        return message;
    }

    public <T extends SendDynamicEmailResponse> T setMessage(String message) {
        this.message = message;
        return (T) this;
    }

    @Override
    public String toString() {
        return "SendDynamicEmailResponse{" +
                "sent=" + sent +
                ", status=" + status +
                ", responseBody='" + message + '\'' +
                '}';
    }
}
