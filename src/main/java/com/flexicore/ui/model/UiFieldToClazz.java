package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.Clazz;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class UiFieldToClazz extends Baselink {
    static UiFieldToClazz s_Singleton = new UiFieldToClazz();
    public static UiFieldToClazz s() {
        return s_Singleton;
    }




    @Override
    @ManyToOne(targetEntity =UiField.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public UiField getLeftside() {
        return (UiField) super.getLeftside();
    }

    public void setLeftside(UiField leftside) {
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
}
