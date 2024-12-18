/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import com.flexicore.model.Clazz;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SuppressWarnings("serial")

@Entity
public class  LicensingEntity extends Baseclass {


	private String canonicalName;


	public LicensingEntity() {
	}


	public String getCanonicalName() {
		return canonicalName;
	}

	@Transient
	@JsonIgnore
	public Clazz getClazz(){
		List<String> list = Optional.ofNullable(getCanonicalName()).map(f -> f.split("\\.")).stream().flatMap(Arrays::stream).toList();
		return list.isEmpty()?null:new Clazz(list.getLast());
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}



}
