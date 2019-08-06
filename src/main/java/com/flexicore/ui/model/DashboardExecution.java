package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class DashboardExecution extends Baseclass {
    static DashboardExecution s_Singleton = new DashboardExecution();
    public static DashboardExecution s() {
        return s_Singleton;
    }

    @ManyToOne(targetEntity = DynamicExecution.class)
    private DynamicExecution dynamicExecution;


    @ManyToOne(targetEntity = DashboardElement.class)
    private DashboardElement dashboardElement;

    @ManyToOne(targetEntity = DashboardElement.class)
    public DashboardElement getDashboardElement() {
        return dashboardElement;
    }

    public <T extends DashboardExecution> T setDashboardElement(DashboardElement dashboardElement) {
        this.dashboardElement = dashboardElement;
        return (T) this;
    }

    @ManyToOne(targetEntity = DynamicExecution.class)
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends DashboardExecution> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }
}
