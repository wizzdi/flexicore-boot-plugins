/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.model;


import javax.persistence.Entity;


@SuppressWarnings("serial")

@Entity

public class LicenseRequestToQuantityFeature extends LicenseRequestToFeature {


	private int quantityLimit;

	public LicenseRequestToQuantityFeature() {
	}


	public int getQuantityLimit() {
		return quantityLimit;
	}

	public <T extends LicenseRequestToQuantityFeature> T setQuantityLimit(int quantityLimit) {
		this.quantityLimit = quantityLimit;
		return (T) this;
	}
}
