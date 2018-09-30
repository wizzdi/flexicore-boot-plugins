package com.flexicore.scheduling.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ServiceCanonicalName {

    @Id
    private String id;

    @ManyToOne(targetEntity = ScheduleAction.class)
    private ScheduleAction scheduleAction;

    private String serviceCanonicalName;

    public String getId() {
        return id;
    }

    public ServiceCanonicalName setId(String id) {
        this.id = id;
        return this;
    }

    public String getServiceCanonicalName() {
        return serviceCanonicalName;
    }

    public ServiceCanonicalName setServiceCanonicalName(String serviceCanonicalName) {
        this.serviceCanonicalName = serviceCanonicalName;
        return this;
    }

    @ManyToOne(targetEntity = ScheduleAction.class)
    public ScheduleAction getScheduleAction() {
        return scheduleAction;
    }

    public ServiceCanonicalName setScheduleAction(ScheduleAction scheduleAction) {
        this.scheduleAction = scheduleAction;
        return this;
    }
}
