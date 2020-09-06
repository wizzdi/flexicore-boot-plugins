package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CellContent extends Baseclass {

    @JsonIgnore
    @OneToMany(targetEntity = CellContentElement.class,mappedBy = "cellContent")
    private List<CellContentElement> cellContentElements=new ArrayList<>();

    public CellContent() {
        super();
    }

    public CellContent(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    @JsonIgnore
    @OneToMany(targetEntity = CellContentElement.class,mappedBy = "cellContent")
    public List<CellContentElement> getCellContentElements() {
        return cellContentElements;
    }

    public <T extends CellContent> T setCellContentElements(List<CellContentElement> cellContentElements) {
        this.cellContentElements = cellContentElements;
        return (T) this;
    }
}
