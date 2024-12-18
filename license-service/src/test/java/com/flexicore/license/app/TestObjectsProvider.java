package com.flexicore.license.app;

import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicenseRequestCreate;
import com.flexicore.license.request.LicensingFeatureCreate;
import com.flexicore.license.request.LicensingProductCreate;
import com.flexicore.license.service.LicenseRequestService;
import com.flexicore.license.service.LicensingFeatureService;
import com.flexicore.license.service.LicensingProductService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestObjectsProvider {

	@Autowired
	private LicensingProductService licensingProductService;
	@Autowired
	private LicensingFeatureService licensingFeatureService;
	@Autowired
	private LicenseRequestService licenseRequestService;
	@Autowired
	private SecurityContext adminSecurityContext;

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public LicensingProduct licensingProduct(){
		return licensingProductService.createLicensingProduct(new LicensingProductCreate().setCanonicalName("test").setName("test"),adminSecurityContext);
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public LicensingFeature licensingFeature(LicensingProduct licensingProduct){
		return licensingFeatureService.createLicensingFeature(new LicensingFeatureCreate().setLicensingProduct(licensingProduct).setCanonicalName("test").setName("test"),adminSecurityContext);
	}


	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public LicenseRequest licenseRequest(){
		LicenseRequestCreate test = new LicenseRequestCreate()
				.setLicensedTenantId(adminSecurityContext.getTenantToCreateIn().getId())
				.setName("test");
		licenseRequestService.validateCreate(test,adminSecurityContext);
		return licenseRequestService.createLicenseRequest(test,adminSecurityContext);
	}
}
