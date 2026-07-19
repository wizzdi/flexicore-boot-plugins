package com.wizzdi.basic.iot.tester.service;

public class IotTestFailureException extends RuntimeException {
    public IotTestFailureException(String message) { super(message); }
    public IotTestFailureException(String message, Throwable cause) { super(message, cause); }
}
