package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.TimedLink;
import com.flexicore.product.model.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class SupplierToProduct extends TimedLink {
    static SupplierToProduct s_Singleton = new SupplierToProduct();
    public static SupplierToProduct s() {
        return s_Singleton;
    }





    @Override
    @ManyToOne(targetEntity = Supplier.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Supplier getLeftside() {
        return (Supplier) super.getLeftside();
    }

    public void setLeftside(Supplier leftside) {
        super.setLeftside(leftside);
    }

    @Override
    public void setLeftside(Baseclass leftside) {
        super.setLeftside(leftside);
    }

    @Override
    @ManyToOne(targetEntity = Product.class, cascade = {CascadeType.MERGE ,CascadeType.PERSIST})
    public Product getRightside() {
        return (Product) super.getRightside();
    }

    public void setRightside(Product rightside) {
        super.setRightside(rightside);
    }

    @Override
    public void setRightside(Baseclass rightside) {
        super.setRightside(rightside);
    }


}
