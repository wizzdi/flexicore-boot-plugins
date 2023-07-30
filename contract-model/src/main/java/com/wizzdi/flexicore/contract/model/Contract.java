package com.wizzdi.flexicore.contract.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.billing.model.payment.PaymentMethod;
import com.flexicore.model.SecuredBasic;
import com.flexicore.organization.model.Customer;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Contract extends SecuredBasic {

    @JsonIgnore
    @OneToMany(targetEntity = ContractItem.class,mappedBy = "contract")
    private List<ContractItem> contractItems=new ArrayList<>();
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;
    @ManyToOne(targetEntity = PaymentMethod.class)
    private PaymentMethod automaticPaymentMethod;

    public Contract() {
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

    @ManyToOne(targetEntity = PaymentMethod.class)
    public PaymentMethod getAutomaticPaymentMethod() {
        return automaticPaymentMethod;
    }

    public <T extends Contract> T setAutomaticPaymentMethod(PaymentMethod automaticPaymentMethod) {
        this.automaticPaymentMethod = automaticPaymentMethod;
        return (T) this;
    }
}
