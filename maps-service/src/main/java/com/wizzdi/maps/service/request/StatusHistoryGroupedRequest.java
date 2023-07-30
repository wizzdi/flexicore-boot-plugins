package com.wizzdi.maps.service.request;

import com.wizzdi.maps.model.MappedPOI;

import java.time.OffsetDateTime;

public class StatusHistoryGroupedRequest {

    private MappedPOIFilter mappedPOIFilter;
    private OffsetDateTime statusAtDate;

    public MappedPOIFilter getMappedPOIFilter() {
        return mappedPOIFilter;
    }

    public <T extends StatusHistoryGroupedRequest> T setMappedPOIFilter(MappedPOIFilter mappedPOIFilter) {
        this.mappedPOIFilter = mappedPOIFilter;
        return (T) this;
    }

    public OffsetDateTime getStatusAtDate() {
        return statusAtDate;
    }

    public <T extends StatusHistoryGroupedRequest> T setStatusAtDate(OffsetDateTime statusAtDate) {
        this.statusAtDate = statusAtDate;
        return (T) this;
    }
}
