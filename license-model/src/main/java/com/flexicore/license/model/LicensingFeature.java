/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.model;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;


@SuppressWarnings("serial")
@Entity

public class LicensingFeature extends LicensingEntity {


	@ManyToOne(targetEntity = LicensingProduct.class)
	private LicensingProduct product;


	public LicensingFeature() {
	}



	@ManyToOne(targetEntity = LicensingProduct.class)
	public LicensingProduct getProduct() {
		return product;
	}

	public void setProduct(LicensingProduct product) {
		this.product = product;
	}


}
