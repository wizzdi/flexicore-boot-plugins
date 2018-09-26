package com.flexicore.scheduling.containers.request;


import com.flexicore.model.dynamic.ExecutionParametersHolder;

public class CreateSchedulingAction {

    private String name;
    private String description;
    private String serviceCanonicalName;
    private String methodName;
    private ExecutionParametersHolder executionParametersHolder;


    public String getServiceCanonicalName() {
        return serviceCanonicalName;
    }

    public CreateSchedulingAction setServiceCanonicalName(String serviceCanonicalName) {
        this.serviceCanonicalName = serviceCanonicalName;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public CreateSchedulingAction setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }


    public String getName() {
        return name;
    }

    public CreateSchedulingAction setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateSchedulingAction setDescription(String description) {
        this.description = description;
        return this;
    }

    public ExecutionParametersHolder getExecutionParametersHolder() {
        return executionParametersHolder;
    }

    public CreateSchedulingAction setExecutionParametersHolder(ExecutionParametersHolder executionParametersHolder) {
        this.executionParametersHolder = executionParametersHolder;
        return this;
    }
}
