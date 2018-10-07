package com.flexicore.scheduling.containers.response;

import java.util.Set;

public class ExecuteScheduleResponse {

    private Set<String> executedLinks;

    public Set<String> getExecutedLinks() {
        return executedLinks;
    }

    public ExecuteScheduleResponse setExecutedLinks(Set<String> executedLinks) {
        this.executedLinks = executedLinks;
        return this;
    }
}
