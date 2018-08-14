package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.Clazz;
import com.flexicore.model.Role;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class PresetToClazz extends Baselink {
    static PresetToClazz s_Singleton = new PresetToClazz();
    public static PresetToClazz s() {
        return s_Singleton;
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
    @ManyToOne(targetEntity =Clazz.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Clazz getRightside() {
        return (Clazz) super.getRightside();
    }

    public void setRightside(Clazz rightside) {
        super.setRightside(rightside);
    }

    @Override
    public void setRightside(Baseclass rightside) {
        super.setRightside(rightside);
    }


    public int getPriority() {
        return priority;
    }

    public PresetToClazz setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public PresetToClazz setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
