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

public class LicenseRequestToFeature extends LicenseRequestToEntity {


	public LicenseRequestToFeature() {
	}


	@Override
	@ManyToOne(targetEntity = LicensingFeature.class)
	public LicensingFeature getLicensingEntity() {
		return (LicensingFeature) super.getLicensingEntity();
	}



	@Override
	public <T extends LicenseRequestToEntity> T setLicensingEntity(LicensingEntity licensingEntity) {
		return super.setLicensingEntity(licensingEntity);
	}
}
