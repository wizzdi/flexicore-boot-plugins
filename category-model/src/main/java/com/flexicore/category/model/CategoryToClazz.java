/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;
import com.flexicore.model.Baseclass;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
public class CategoryToClazz extends Baseclass {

	public CategoryToClazz() {
	}


	@ManyToOne(targetEntity = Category.class)
	private Category category;
	private String type;

	@ManyToOne(targetEntity = Category.class)

	public Category getCategory() {
		return category;
	}

	public <T extends CategoryToClazz> T setCategory(Category category) {
		this.category = category;
		return (T) this;
	}


	public String getType() {
		return type;
	}

	public <T extends CategoryToClazz> T setType(String type) {
		this.type = type;
		return (T) this;
	}


	public Clazz getClazz() {
		return new Clazz(type);
	}
}
