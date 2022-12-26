package com.wizzdi.basic.iot.service.response;

import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.flexicore.security.response.PaginationResponse;

import java.util.Collections;

public class ImportGatewaysResponse {

    private PaginationResponse<Gateway> importedGateways=new PaginationResponse<>(Collections.emptyList(),0,0);

    public ImportGatewaysResponse() {
    }

    public ImportGatewaysResponse(PaginationResponse<Gateway> importedGateways) {
        this.importedGateways = importedGateways;
    }

    public PaginationResponse<Gateway> getImportedGateways() {
        return importedGateways;
    }

    public <T extends ImportGatewaysResponse> T setImportedGateways(PaginationResponse<Gateway> importedGateways) {
        this.importedGateways = importedGateways;
        return (T) this;
    }
}
