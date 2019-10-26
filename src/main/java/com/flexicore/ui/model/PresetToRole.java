package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.Role;
import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class PresetToRole extends Baselink {
    static PresetToRole s_Singleton = new PresetToRole();
    public static PresetToRole s() {
        return s_Singleton;
    }

    public PresetToRole() {
    }

    public PresetToRole(String name, SecurityContext securityContext) {
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
    @ManyToOne(targetEntity =Role.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Role getRightside() {
        return (Role) super.getRightside();
    }

    public void setRightside(Role rightside) {
        super.setRightside(rightside);
    }

    @Override
    public void setRightside(Baseclass rightside) {
        super.setRightside(rightside);
    }


    public int getPriority() {
        return priority;
    }

    public PresetToRole setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public PresetToRole setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
