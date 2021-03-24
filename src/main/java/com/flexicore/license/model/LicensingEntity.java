/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.model;



import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;


@SuppressWarnings("serial")

@Entity
public class  LicensingEntity extends SecuredBasic {


	private String canonicalName;


	public LicensingEntity() {
	}


	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}



}
