/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;
import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
public class CategoryToClazz extends SecuredBasic {

	public CategoryToClazz() {
	}


	@ManyToOne(targetEntity = Category.class)
	private Category category;
	@ManyToOne(targetEntity = Baseclass.class)
	private Clazz clazz;

	@ManyToOne(targetEntity = Category.class)

	public Category getCategory() {
		return category;
	}

	public <T extends CategoryToClazz> T setCategory(Category category) {
		this.category = category;
		return (T) this;
	}

	@ManyToOne(targetEntity = Clazz.class)

	public Clazz getClazz() {
		return clazz;
	}

	public <T extends CategoryToClazz> T setClazz(Clazz baseclass) {
		this.clazz = baseclass;
		return (T) this;
	}
}
