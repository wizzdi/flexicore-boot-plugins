package com.wizzdi.maps.service.email.context;

import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EmailEntry {


    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private String date;
    private String time;
    private String tenant;
    private String externalId;
    private String statusName;
    private boolean even;

    public EmailEntry(StatusHistory statusHistory, int index, ZoneOffset zoneOffset) {
        OffsetDateTime dateAtStatus = statusHistory.getDateAtStatus().withOffsetSameInstant(zoneOffset);

        this.date=dateAtStatus.format(DATE_FORMATTER);
        this.time=dateAtStatus.format(TIME_FORMATTER);
        MappedPOI mappedPOI = statusHistory.getMappedPOI();
        if(mappedPOI!=null){
            this.tenant= Optional.ofNullable(mappedPOI.getSecurity()).map(f->f.getTenant()).map(f->f.getName()).orElse(null);
            this.externalId=mappedPOI.getExternalId();
            this.statusName=Optional.ofNullable(mappedPOI.getMapIcon()).map(f->f.getName()).orElse(null);
        }

        this.even=index%2==0;


    }

    public EmailEntry() {
    }

    public String getDate() {
        return date;
    }

    public <T extends EmailEntry> T setDate(String date) {
        this.date = date;
        return (T) this;
    }

    public String getTime() {
        return time;
    }

    public <T extends EmailEntry> T setTime(String time) {
        this.time = time;
        return (T) this;
    }

    public String getTenant() {
        return tenant;
    }

    public <T extends EmailEntry> T setTenant(String tenant) {
        this.tenant = tenant;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends EmailEntry> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    public String getStatusName() {
        return statusName;
    }

    public <T extends EmailEntry> T setStatusName(String statusName) {
        this.statusName = statusName;
        return (T) this;
    }

    public boolean isEven() {
        return even;
    }

    public <T extends EmailEntry> T setEven(boolean even) {
        this.even = even;
        return (T) this;
    }
}
