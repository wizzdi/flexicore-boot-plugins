/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.flexicore.model.SecuredBasic;
import com.flexicore.model.SecurityTenant;
import com.wizzdi.flexicore.file.model.FileResource;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
@Entity

public class LicenseRequest extends SecuredBasic {

	@Lob
	private String macAddress;
	private String diskSerialNumber;
	@Lob
	private String externalHWSerialNumber;
	private boolean signed;
	@ManyToOne(targetEntity = FileResource.class)
	private FileResource license;


	@OneToOne(targetEntity = FileResource.class)
	private FileResource requestFile;

	@OneToMany(targetEntity = LicenseRequestToEntity.class,mappedBy="licenseRequest", fetch= FetchType.LAZY)
	@JsonIgnore
	private List<LicenseRequestToEntity> requestToEntity =new ArrayList<>();


	@ManyToOne(targetEntity = SecurityTenant.class)
	private SecurityTenant licensedTenant;


	public LicenseRequest() {
	}


	@Lob
	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getDiskSerialNumber() {
		return diskSerialNumber;
	}

	public void setDiskSerialNumber(String diskSerialNumber) {
		this.diskSerialNumber = diskSerialNumber;
	}

	@Lob
	public String getExternalHWSerialNumber() {
		return externalHWSerialNumber;
	}

	public void setExternalHWSerialNumber(String externalHWSerialNumber) {
		this.externalHWSerialNumber = externalHWSerialNumber;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	@ManyToOne(targetEntity = FileResource.class)
	public FileResource getLicense() {
		return license;
	}

	public void setLicense(FileResource license) {
		this.license = license;
	}

	@OneToMany(targetEntity = LicenseRequestToEntity.class,mappedBy="licenseRequest", fetch= FetchType.LAZY)
	@JsonIgnore
	public List<LicenseRequestToEntity> getRequestToEntity() {
		return requestToEntity;
	}

	public <T extends LicenseRequest> T setRequestToEntity(List<LicenseRequestToEntity> requestToEntity) {
		this.requestToEntity = requestToEntity;
		return (T) this;
	}

	@ManyToOne(targetEntity = SecurityTenant.class)
	public SecurityTenant getLicensedTenant() {
		return licensedTenant;
	}

	public void setLicensedTenant(SecurityTenant licensedTenant) {
		this.licensedTenant = licensedTenant;
	}

	@OneToOne(targetEntity = FileResource.class)
	public FileResource getRequestFile() {
		return requestFile;
	}

	public void setRequestFile(FileResource requestFile) {
		this.requestFile = requestFile;
	}
}
