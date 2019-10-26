package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.Tenant;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class PresetToTenant extends Baselink {
    static PresetToTenant s_Singleton = new PresetToTenant();
    public static PresetToTenant s() {
        return s_Singleton;
    }

    public PresetToTenant() {
    }

    public PresetToTenant(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    private int priority;
    private boolean enabled;




    @Override
    @ManyToOne(targetEntity =Preset.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Preset getLeftside() {
        return (Preset) super.getLeftside();
    }

    public void setLeftside(Preset leftside) {
        super.setLeftside(leftside);
    }

    @Override
    public void setLeftside(Baseclass leftside) {
        super.setLeftside(leftside);
    }

    @Override
    @ManyToOne(targetEntity =Tenant.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Tenant getRightside() {
        return (Tenant) super.getRightside();
    }

    public void setRightside(Tenant rightside) {
        super.setRightside(rightside);
    }

    @Override
    public void setRightside(Baseclass rightside) {
        super.setRightside(rightside);
    }


    public int getPriority() {
        return priority;
    }

    public PresetToTenant setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public PresetToTenant setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
