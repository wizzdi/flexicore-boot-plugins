/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
public class CategoryToBaseClass extends SecuredBasic {


	@ManyToOne(targetEntity = Category.class)
	private Category category;

	@ManyToOne(targetEntity = Baseclass.class)
	private Baseclass baseclass;

	@ManyToOne(targetEntity = Category.class)

	public Category getCategory() {
		return category;
	}

	public <T extends CategoryToBaseClass> T setCategory(Category category) {
		this.category = category;
		return (T) this;
	}

	@ManyToOne(targetEntity = Baseclass.class)

	public Baseclass getBaseclass() {
		return baseclass;
	}

	public <T extends CategoryToBaseClass> T setBaseclass(Baseclass baseclass) {
		this.baseclass = baseclass;
		return (T) this;
	}
}
