package com.flexicore.license.request;


import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.Set;

/**
 * Created by Asaf on 17/10/2016.
 */
public class LicensingEntityFiltering extends PaginationFilter {

    private BasicPropertiesFilter basicPropertiesFilter;

    private Set<String> canonicalNames;

    public Set<String> getCanonicalNames() {
        return canonicalNames;
    }

    public <T extends LicensingEntityFiltering> T setCanonicalNames(Set<String> canonicalNames) {
        this.canonicalNames = canonicalNames;
        return (T) this;
    }

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends LicensingEntityFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }
}
