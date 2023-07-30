package com.wizzdi.maps.service.response;

import com.wizzdi.maps.model.MapIcon;

public class StatusHistoryGroupedEntry {

    private MapIcon mapIcon;
    private long count;

    public MapIcon getMapIcon() {
        return mapIcon;
    }

    public <T extends StatusHistoryGroupedEntry> T setMapIcon(MapIcon mapIcon) {
        this.mapIcon = mapIcon;
        return (T) this;
    }

    public long getCount() {
        return count;
    }

    public <T extends StatusHistoryGroupedEntry> T setCount(long count) {
        this.count = count;
        return (T) this;
    }

    @Override
    public String toString() {
        return "StatusHistoryGroupedEntry{" +
                "mapIcon=" + mapIcon +
                ", count=" + count +
                '}';
    }
}
