package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.organization.model.SalesPersonToRegion;
import com.flexicore.organization.request.SalesPersonToRegionCreate;
import com.flexicore.organization.service.SalesPersonToRegionService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


@OperationsInside
@RequestMapping("/plugins/SalesPersonToRegion")
@Tag(name = "SalesPersonToRegion")
@Extension
@RestController
public class SalesPersonToRegionController implements Plugin {


	@Autowired
	private SalesPersonToRegionService service;


	@PostMapping("/createSalesPersonToRegion")
	@Operation(summary = "createSalesPersonToRegion", description = "Creates SalesPersonToRegion")
	@IOperation(Name = "createSalesPersonToRegion", Description = "Creates SalesPersonToRegion")
	public SalesPersonToRegion createSalesPersonToRegion(

			@RequestBody  SalesPersonToRegionCreate creationContainer,
			@RequestAttribute SecurityContext securityContext) {

		service.validate(creationContainer, securityContext);

		return service.createSalesPersonToRegion(creationContainer,
				securityContext);
	}

}
