package com.wizzdi.maps.service.response;

public class MapFilterComponent {

    private String name;
    private String id;
    private long count;

    public String getId() {
        return id;
    }

    public <T extends MapFilterComponent> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public <T extends MapFilterComponent> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public long getCount() {
        return count;
    }

    public <T extends MapFilterComponent> T setCount(long count) {
        this.count = count;
        return (T) this;
    }
}
