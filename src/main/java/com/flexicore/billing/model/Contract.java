package com.flexicore.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Customer;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Contract extends Baseclass {

    @JsonIgnore
    @OneToMany(targetEntity = ContractItem.class,mappedBy = "contract")
    private List<ContractItem> contractItems=new ArrayList<>();
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

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

    @ManyToOne(targetEntity = Customer.class)
    public Customer getCustomer() {
        return customer;
    }

    public <T extends Contract> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }
}
