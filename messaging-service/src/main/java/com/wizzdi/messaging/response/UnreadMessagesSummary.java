package com.wizzdi.messaging.response;

import java.util.ArrayList;
import java.util.List;

public class UnreadMessagesSummary {

    private List<UnreadMessagesSummaryItem> items=new ArrayList<>();
    private long total;

    public List<UnreadMessagesSummaryItem> getItems() {
        return items;
    }

    public <T extends UnreadMessagesSummary> T setItems(List<UnreadMessagesSummaryItem> items) {
        this.items = items;
        return (T) this;
    }

    public long getTotal() {
        return total;
    }

    public <T extends UnreadMessagesSummary> T setTotal(long total) {
        this.total = total;
        return (T) this;
    }
}
