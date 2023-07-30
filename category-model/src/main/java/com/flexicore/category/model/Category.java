/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.category.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
@Entity

public class Category extends SecuredBasic {

	@OneToMany(targetEntity = CategoryToBaseClass.class,mappedBy="category")
	@JsonIgnore
	private List<CategoryToBaseClass> categoryToBaseclass =new ArrayList<>();

	@JsonIgnore
	@OneToMany(targetEntity = CategoryToClazz.class,mappedBy="category")
	private List<CategoryToClazz> clazzes=new ArrayList<>();


	@JsonIgnore
	@OneToMany(targetEntity = CategoryToBaseClass.class,mappedBy="category")
	public List<CategoryToBaseClass> getCategoryToBaseclass() {
		return categoryToBaseclass;
	}


	public void setCategoryToBaseclass(List<CategoryToBaseClass> baseclasses) {
		this.categoryToBaseclass = baseclasses;
	}


	@JsonIgnore
	@OneToMany(targetEntity = CategoryToClazz.class,mappedBy="category")

	public List<CategoryToClazz> getClazzes() {
		return clazzes;
	}


	public void setClazzes(List<CategoryToClazz> clazzes) {
		this.clazzes = clazzes;
	}
	
	

}
