package com.flexicore.ui.dashboard.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class CellContent extends Baseclass {


    public CellContent() {
        super();
    }

    public CellContent(String name, SecurityContext securityContext) {
        super(name, securityContext);
    }
}
