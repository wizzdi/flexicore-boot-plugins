package com.flexicore.organization.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.organization.model.Industry;
import com.flexicore.organization.request.IndustryFiltering;
import com.flexicore.organization.service.IndustryService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


@OperationsInside
@RequestMapping("/plugins/UnsecureIndustry")
@Tag(name = "Industry Unsecure")
@Extension
@RestController
public class IndustryUnsecureController implements Plugin {


	@Autowired
	private IndustryService service;



	@Operation(summary = "getAllIndustries", description = "Lists all Industries")
	@IOperation(Name = "getAllIndustries", Description = "Lists all Industries")
	@PostMapping("/getAllIndustries")
	public PaginationResponse<Industry> getAllIndustries(
			@RequestHeader("authenticationKey") String authenticationKey, @RequestBody IndustryFiltering filtering) {

		service.validateFiltering(filtering, null);
		return service.getAllIndustries(null, filtering);
	}


}