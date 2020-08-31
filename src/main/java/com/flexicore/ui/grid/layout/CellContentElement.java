package com.flexicore.ui.grid.layout;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CellContentElement extends Baseclass {

    @ManyToOne(targetEntity = CellContent.class)
    private CellContent cellContent;
    private String contextString;

    @ManyToOne(targetEntity = CellContent.class)
    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellContentElement> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }

    public String getContextString() {
        return contextString;
    }

    public <T extends CellContentElement> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }
}
