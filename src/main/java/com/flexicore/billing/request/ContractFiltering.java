package com.flexicore.billing.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.DateFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.Set;

public class ContractFiltering extends PaginationFilter {
    private BasicPropertiesFilter basicPropertiesFilter;
    private DateFilter nextChargeDate;

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends ContractFiltering> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public DateFilter getNextChargeDate() {
        return nextChargeDate;
    }

    public <T extends ContractFiltering> T setNextChargeDate(DateFilter nextChargeDate) {
        this.nextChargeDate = nextChargeDate;
        return (T) this;
    }

}
