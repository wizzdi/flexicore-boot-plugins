package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.territories.Address;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class BranchToAddress extends Baselink {
    static BranchToAddress s_Singleton = new BranchToAddress();
    public static BranchToAddress s() {
        return s_Singleton;
    }





    @Override
    @ManyToOne(targetEntity = Branch.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Branch getLeftside() {
        return (Branch) super.getLeftside();
    }

    public void setLeftside(Branch leftside) {
        super.setLeftside(leftside);
    }

    @Override
    public void setLeftside(Baseclass leftside) {
        super.setLeftside(leftside);
    }

    @Override
    @ManyToOne(targetEntity = Address.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Address getRightside() {
        return (Address) super.getRightside();
    }

    public void setRightside(Address rightside) {
        super.setRightside(rightside);
    }

    @Override
    public void setRightside(Baseclass rightside) {
        super.setRightside(rightside);
    }


}
