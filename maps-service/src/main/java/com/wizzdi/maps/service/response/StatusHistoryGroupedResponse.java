package com.wizzdi.maps.service.response;

import java.util.List;

public class StatusHistoryGroupedResponse {

    private List<StatusHistoryGroupedEntry> statusHistoryGroupedEntries;

    public List<StatusHistoryGroupedEntry> getStatusHistoryGroupedEntries() {
        return statusHistoryGroupedEntries;
    }

    public <T extends StatusHistoryGroupedResponse> T setStatusHistoryGroupedEntries(List<StatusHistoryGroupedEntry> statusHistoryGroupedEntries) {
        this.statusHistoryGroupedEntries = statusHistoryGroupedEntries;
        return (T) this;
    }

    @Override
    public String toString() {
        return "StatusHistoryGroupedResponse{" +
                "statusHistoryGroupedEntries=" + statusHistoryGroupedEntries +
                '}';
    }
}
