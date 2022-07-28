package com.wizzdi.basic.iot.service.request;

import com.wizzdi.basic.iot.service.validators.ValidVersion;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.Set;

public class FirmwareUpdateFilter extends PaginationFilter {


    @ValidVersion
    private Set<String> versions;
    private BasicPropertiesFilter basicPropertiesFilter;

    public Set<String> getVersions() {
        return versions;
    }

    public <T extends FirmwareUpdateFilter> T setVersions(Set<String> versions) {
        this.versions = versions;
        return (T) this;
    }

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends FirmwareUpdateFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }
}
