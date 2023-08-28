package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.StateHistory;


public class StateHistoryUpdate extends StateHistoryCreate{

    private String id;
    @JsonIgnore
    private StateHistory stateHistory;


    public String getId() {
        return id;
    }

    public <T extends StateHistoryUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public StateHistory getStateHistory() {
        return stateHistory;
    }

    public <T extends StateHistoryUpdate> T setStateHistory(StateHistory stateHistory) {
        this.stateHistory = stateHistory;
        return (T) this;
    }
}
