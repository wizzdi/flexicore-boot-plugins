package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CellContentElement extends Baseclass {

    @ManyToOne(targetEntity = CellContent.class)
    private CellContent cellContent;

    @JsonIgnore
    @OneToMany(targetEntity = DataMapper.class,mappedBy = "cellContentElement")
    private List<DataMapper> dataMappers=new ArrayList<>();


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

    @JsonIgnore
    @OneToMany(targetEntity = DataMapper.class,mappedBy = "cellContentElement")
    public List<DataMapper> getDataMappers() {
        return dataMappers;
    }

    public <T extends CellContentElement> T setDataMappers(List<DataMapper> dataMappers) {
        this.dataMappers = dataMappers;
        return (T) this;
    }
}
