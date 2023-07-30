package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;

public class ImportGatewaysRequest {

    private String csvId;
    @JsonIgnore
    private FileResource csv;

    public String getCsvId() {
        return csvId;
    }

    public <T extends ImportGatewaysRequest> T setCsvId(String csvId) {
        this.csvId = csvId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getCsv() {
        return csv;
    }

    public <T extends ImportGatewaysRequest> T setCsv(FileResource csv) {
        this.csv = csv;
        return (T) this;
    }
}
