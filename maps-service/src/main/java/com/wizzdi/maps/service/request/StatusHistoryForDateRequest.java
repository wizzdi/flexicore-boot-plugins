package com.wizzdi.maps.service.request;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class StatusHistoryForDateRequest {

    @NotNull
    private MappedPOIFilter mappedPOIFilter;
    @NotNull
    private OffsetDateTime statusAtDate;

    public StatusHistoryForDateRequest() {
    }

    public StatusHistoryForDateRequest(StatusHistoryGroupedRequest other) {
        this.mappedPOIFilter = other.getMappedPOIFilter();
        this.statusAtDate = other.getStatusAtDate();
    }

    public StatusHistoryForDateRequest(StatusHistoryForDateRequest other) {
        this.mappedPOIFilter = other.mappedPOIFilter;
        this.statusAtDate = other.statusAtDate;
    }

    public MappedPOIFilter getMappedPOIFilter() {
        return mappedPOIFilter;
    }

    public <T extends StatusHistoryForDateRequest> T setMappedPOIFilter(MappedPOIFilter mappedPOIFilter) {
        this.mappedPOIFilter = mappedPOIFilter;
        return (T) this;
    }

    public OffsetDateTime getStatusAtDate() {
        return statusAtDate;
    }

    public <T extends StatusHistoryForDateRequest> T setStatusAtDate(OffsetDateTime statusAtDate) {
        this.statusAtDate = statusAtDate;
        return (T) this;
    }
}
