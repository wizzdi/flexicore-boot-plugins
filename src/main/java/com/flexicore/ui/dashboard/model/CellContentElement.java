package com.flexicore.ui.dashboard.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class CellContentElement extends Baseclass {

    @ManyToOne(targetEntity = CellContent.class)
    private CellContent cellContent;

    public CellContentElement() {
        super();
    }

    public CellContentElement(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    @ManyToOne(targetEntity = CellContent.class)
    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellContentElement> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }

}
