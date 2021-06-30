package com.flexicore.billing.response;

import java.util.List;

public class ActivateContractItemsResponse {

    private List<ActivateContractItemResponse> activateContractItemResponse;


    public List<ActivateContractItemResponse> getActivateContractItemResponse() {
        return activateContractItemResponse;
    }

    public <T extends ActivateContractItemsResponse> T setActivateContractItemResponse(List<ActivateContractItemResponse> activateContractItemResponse) {
        this.activateContractItemResponse = activateContractItemResponse;
        return (T) this;
    }
}
