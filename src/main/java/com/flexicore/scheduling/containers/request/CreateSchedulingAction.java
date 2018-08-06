package com.flexicore.scheduling.containers.request;

public class CreateSchedulingAction {


    private String serviceCanonicalName;
    private String methodName;
    private String parameter1;
    private String parameter2;
    private String parameter3;


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

    public String getParameter1() {
        return parameter1;
    }

    public CreateSchedulingAction setParameter1(String parameter1) {
        this.parameter1 = parameter1;
        return this;
    }

    public String getParameter2() {
        return parameter2;
    }

    public CreateSchedulingAction setParameter2(String parameter2) {
        this.parameter2 = parameter2;
        return this;
    }

    public String getParameter3() {
        return parameter3;
    }

    public CreateSchedulingAction setParameter3(String parameter3) {
        this.parameter3 = parameter3;
        return this;
    }
}
