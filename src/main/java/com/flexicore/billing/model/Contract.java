package com.flexicore.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Contract extends Baseclass {

    @JsonIgnore
    @OneToMany(targetEntity = ContractItem.class,mappedBy = "contract")
    private List<ContractItem> contractItems=new ArrayList<>();

    public Contract() {
    }

    public Contract(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    @JsonIgnore
    @OneToMany(targetEntity = ContractItem.class,mappedBy = "contract")
    public List<ContractItem> getContractItems() {
        return contractItems;
    }

    public <T extends Contract> T setContractItems(List<ContractItem> contractItems) {
        this.contractItems = contractItems;
        return (T) this;
    }
}
