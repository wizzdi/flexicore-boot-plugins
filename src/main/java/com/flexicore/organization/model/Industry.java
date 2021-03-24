package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Industry extends SecuredBasic {

    @JsonIgnore
    @OneToMany(targetEntity = IndustryToCustomer.class,mappedBy = "industry")
    private List<IndustryToCustomer> industryToCustomers=new ArrayList<>();

    @JsonIgnore
    @OneToMany(targetEntity = IndustryToCustomer.class,mappedBy = "industry")
    public List<IndustryToCustomer> getIndustryToCustomers() {
        return industryToCustomers;
    }

    public <T extends Industry> T setIndustryToCustomers(List<IndustryToCustomer> industryToCustomers) {
        this.industryToCustomers = industryToCustomers;
        return (T) this;
    }
}
