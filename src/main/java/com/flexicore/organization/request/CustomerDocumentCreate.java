package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;
import com.flexicore.organization.model.Customer;
import com.flexicore.request.BaseclassCreate;
import com.flexicore.request.UserCreate;

public class CustomerDocumentCreate extends BaseclassCreate {

    private String fileResourceId;
    @JsonIgnore
    private FileResource fileResource;
    private String customerId;
    @JsonIgnore
    private Customer customer;

    public String getFileResourceId() {
        return fileResourceId;
    }

    public <T extends CustomerDocumentCreate> T setFileResourceId(String fileResourceId) {
        this.fileResourceId = fileResourceId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends CustomerDocumentCreate> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
        return (T) this;
    }

    public String getCustomerId() {
        return customerId;
    }

    public <T extends CustomerDocumentCreate> T setCustomerId(String customerId) {
        this.customerId = customerId;
        return (T) this;
    }

    @JsonIgnore
    public Customer getCustomer() {
        return customer;
    }

    public <T extends CustomerDocumentCreate> T setCustomer(Customer customer) {
        this.customer = customer;
        return (T) this;
    }
}
