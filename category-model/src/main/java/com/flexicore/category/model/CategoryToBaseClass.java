/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
public class CategoryToBaseClass extends Baseclass {


	@ManyToOne(targetEntity = Category.class)
	private Category category;


	private String baseclassId;
	private String baseclassType;

	@ManyToOne(targetEntity = Category.class)

	public Category getCategory() {
		return category;
	}

	public <T extends CategoryToBaseClass> T setCategory(Category category) {
		this.category = category;
		return (T) this;
	}

	public String getBaseclassId() {
		return baseclassId;
	}

	public <T extends CategoryToBaseClass> T setBaseclassId(String baseclassId) {
		this.baseclassId = baseclassId;
		return (T) this;
	}

	public String getBaseclassType() {
		return baseclassType;
	}

	public <T extends CategoryToBaseClass> T setBaseclassType(String baseclassType) {
		this.baseclassType = baseclassType;
		return (T) this;
	}
}
