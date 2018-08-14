package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.Clazz;
import com.flexicore.model.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class PresetToUser extends Baselink {
    static PresetToUser s_Singleton = new PresetToUser();
    public static PresetToUser s() {
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
    @ManyToOne(targetEntity =User.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public User getRightside() {
        return (User) super.getRightside();
    }

    public void setRightside(User rightside) {
        super.setRightside(rightside);
    }

    @Override
    public void setRightside(Baseclass rightside) {
        super.setRightside(rightside);
    }


    public int getPriority() {
        return priority;
    }

    public PresetToUser setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public PresetToUser setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
