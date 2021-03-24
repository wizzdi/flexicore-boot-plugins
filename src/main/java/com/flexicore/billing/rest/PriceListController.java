package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.billing.model.PriceList;
import com.flexicore.billing.model.PriceList_;
import com.flexicore.billing.request.PriceListCreate;
import com.flexicore.billing.request.PriceListFiltering;
import com.flexicore.billing.request.PriceListUpdate;
import com.flexicore.billing.service.PriceListService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@OperationsInside

@RequestMapping("/plugins/PriceList")

@Tag(name = "PriceList")
@Extension
@RestController
public class PriceListController implements Plugin {

		@Autowired
	private PriceListService service;



	@Operation(summary = "getAllPriceLists", description = "Lists all PriceLists")
	@IOperation(Name = "getAllPriceLists", Description = "Lists all PriceLists")
	@PostMapping("/getAllPriceLists")
	public PaginationResponse<PriceList> getAllPriceLists(

			@RequestBody PriceListFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllPriceLists(securityContext, filtering);
	}



	@PostMapping("/createPriceList")
	@Operation(summary = "createPriceList", description = "Creates PriceList")
	@IOperation(Name = "createPriceList", Description = "Creates PriceList")
	public PriceList createPriceList(

			@RequestBody 	PriceListCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createPriceList(creationContainer, securityContext);
	}



	@PutMapping("/updatePriceList")
	@Operation(summary = "updatePriceList", description = "Updates PriceList")
	@IOperation(Name = "updatePriceList", Description = "Updates PriceList")
	public PriceList updatePriceList(

			@RequestBody PriceListUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		PriceList priceList = service.getByIdOrNull(updateContainer.getId(),
				PriceList.class, PriceList_.security, securityContext);
		if (priceList == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no PriceList with id "
					+ updateContainer.getId());
		}
		updateContainer.setPriceList(priceList);

		return service.updatePriceList(updateContainer, securityContext);
	}
}