package com.flexicore.scheduling.request;

public class NullActionBody {
    private String value1;
    private Long value2;

    public String getValue1() {
        return value1;
    }

    public <T extends NullActionBody> T setValue1(String value1) {
        this.value1 = value1;
        return (T) this;
    }

    public Long getValue2() {
        return value2;
    }

    public <T extends NullActionBody> T setValue2(Long value2) {
        this.value2 = value2;
        return (T) this;
    }
}
