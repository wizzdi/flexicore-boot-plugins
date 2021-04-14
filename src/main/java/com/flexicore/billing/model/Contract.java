package com.flexicore.billing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.flexicore.organization.model.Customer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
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
    private OffsetDateTime nextChargeDate;

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

    public OffsetDateTime getNextChargeDate() {
        return nextChargeDate;
    }

    public <T extends Contract> T setNextChargeDate(OffsetDateTime firstChargeDate) {
        this.nextChargeDate = firstChargeDate;
        return (T) this;
    }
}
