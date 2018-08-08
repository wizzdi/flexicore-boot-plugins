package com.flexicore.scheduling.containers.request;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

public class SchduelingInvocationContext implements InvocationContext {
    private Object[] parameters;

    public SchduelingInvocationContext(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public Constructor<?> getConstructor() {
        return null;
    }

    @Override
    public Object[] getParameters() throws IllegalStateException {
        return parameters;
    }

    @Override
    public void setParameters(Object[] objects) throws IllegalStateException, IllegalArgumentException {

    }

    @Override
    public Map<String, Object> getContextData() {
        return null;
    }

    @Override
    public Object getTimer() {
        return null;
    }

    @Override
    public Object proceed() throws Exception {
        return null;
    }
}
