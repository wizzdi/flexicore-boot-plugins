package com.flexicore.organization.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.file.model.FileResource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class CustomerDocument extends SecuredBasic {

    @OneToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;


    @OneToOne(targetEntity = FileResource.class)

    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends CustomerDocument> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
        return (T) this;
    }

    @ManyToOne(targetEntity = Customer.class)
    public Customer getCustomer() {
        return customer;
    }

    public <T extends CustomerDocument> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }
}
