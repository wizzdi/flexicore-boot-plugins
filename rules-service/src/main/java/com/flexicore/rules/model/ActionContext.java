package com.flexicore.rules.model;

import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokerRequest;

public class ActionContext {

    private String id;

    private ExecuteInvokerRequest executeInvokerRequest;

    public ActionContext(String id, ExecuteInvokerRequest executeInvokerRequest) {
        this.id = id;
        this.executeInvokerRequest = executeInvokerRequest;
    }

    public ActionContext() {
    }

    public String getId() {
        return id;
    }

    public <T extends ActionContext> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public ExecuteInvokerRequest getExecuteInvokerRequest() {
        return executeInvokerRequest;
    }

    public <T extends ActionContext> T setExecuteInvokerRequest(ExecuteInvokerRequest executeInvokerRequest) {
        this.executeInvokerRequest = executeInvokerRequest;
        return (T) this;
    }
}
