package com.wizzdi.flexicore.dynamic.invoker.service.email.context;

import java.util.ArrayList;
import java.util.List;

public class EmailContext {
    private List<EmailEntry> entries=new ArrayList<>();

    public EmailContext(List<EmailEntry> entries) {
        this.entries = entries;
    }

    public EmailContext() {
    }

    public List<EmailEntry> getEntries() {
        return entries;
    }

    public <T extends EmailContext> T setEntries(List<EmailEntry> entries) {
        this.entries = entries;
        return (T) this;
    }
}
