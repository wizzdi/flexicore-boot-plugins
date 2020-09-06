package com.flexicore.ui.dashboard.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DataMapper extends Baseclass {

    @ManyToOne(targetEntity = CellToLayout.class)
    private CellToLayout cellToLayout;
    @ManyToOne(targetEntity = DynamicExecution.class)
    private DynamicExecution dynamicExecution;
    private String fieldPath;
    private String staticData;
    @ManyToOne(targetEntity = CellContentElement.class)
    private CellContentElement cellContentElement;

    public DataMapper() {
        super();
    }

    public DataMapper(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }

    @ManyToOne(targetEntity = CellToLayout.class)
    public CellToLayout getCellToLayout() {
        return cellToLayout;
    }

    public <T extends DataMapper> T setCellToLayout(CellToLayout cellToLayout) {
        this.cellToLayout = cellToLayout;
        return (T) this;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public <T extends DataMapper> T setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
        return (T) this;
    }

    @ManyToOne(targetEntity = CellContentElement.class)
    public CellContentElement getCellContentElement() {
        return cellContentElement;
    }

    public <T extends DataMapper> T setCellContentElement(CellContentElement cellContentElement) {
        this.cellContentElement = cellContentElement;
        return (T) this;
    }

    public String getStaticData() {
        return staticData;
    }

    public <T extends DataMapper> T setStaticData(String staticData) {
        this.staticData = staticData;
        return (T) this;
    }

    @ManyToOne(targetEntity = DynamicExecution.class)
    public DynamicExecution getDynamicExecution() {
        return dynamicExecution;
    }

    public <T extends DataMapper> T setDynamicExecution(DynamicExecution dynamicExecution) {
        this.dynamicExecution = dynamicExecution;
        return (T) this;
    }
}
