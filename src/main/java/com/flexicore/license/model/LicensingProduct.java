/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.model;

import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity

public class LicensingProduct extends LicensingEntity {

	@OneToMany(targetEntity = LicensingFeature.class,mappedBy = "product")
	@JsonIgnore
	private List<LicensingFeature> licensingFeatures= new ArrayList<>();

	public LicensingProduct() {
	}


	@OneToMany(targetEntity = LicensingFeature.class,mappedBy = "product")
	@JsonIgnore
	public List<LicensingFeature> getLicensingFeatures() {
		return licensingFeatures;
	}

	public void setLicensingFeatures(List<LicensingFeature> licensingFeatures) {
		this.licensingFeatures = licensingFeatures;
	}
}
