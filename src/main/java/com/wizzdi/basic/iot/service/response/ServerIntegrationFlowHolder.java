package com.wizzdi.basic.iot.service.response;

import com.wizzdi.basic.iot.client.BasicIOTConnection;
import org.springframework.integration.dsl.IntegrationFlow;

public class ServerIntegrationFlowHolder {

    private final IntegrationFlow integrationFlow;
    private final BasicIOTConnection basicIOTConnection;

    public ServerIntegrationFlowHolder(IntegrationFlow integrationFlow, BasicIOTConnection basicIOTConnection) {
        this.integrationFlow = integrationFlow;
        this.basicIOTConnection = basicIOTConnection;
    }

    public IntegrationFlow getIntegrationFlow() {
        return integrationFlow;
    }

    public BasicIOTConnection getBasicIOTConnection() {
        return basicIOTConnection;
    }
}
