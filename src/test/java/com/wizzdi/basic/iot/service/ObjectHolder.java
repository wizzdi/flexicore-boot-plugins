package com.wizzdi.basic.iot.service;

public class ObjectHolder<T>{
    private final T t;

    public ObjectHolder(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }
}
