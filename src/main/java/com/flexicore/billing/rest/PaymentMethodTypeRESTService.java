package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.billing.request.PaymentMethodTypeCreate;
import com.flexicore.billing.request.PaymentMethodTypeFiltering;
import com.flexicore.billing.request.PaymentMethodTypeUpdate;
import com.flexicore.billing.service.PaymentMethodTypeService;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/PaymentMethodType")
@RequestScoped
@Tag(name = "PaymentMethodType")
@Extension
@Component
public class PaymentMethodTypeRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PaymentMethodTypeService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllPaymentMethodTypes", description = "Lists all PaymentMethodTypes")
	@IOperation(Name = "getAllPaymentMethodTypes", Description = "Lists all PaymentMethodTypes")
	@Path("getAllPaymentMethodTypes")
	public PaginationResponse<PaymentMethodType> getAllPaymentMethodTypes(
			@HeaderParam("authenticationKey") String authenticationKey,
			PaymentMethodTypeFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllPaymentMethodTypes(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createPaymentMethodType")
	@Operation(summary = "createPaymentMethodType", description = "Creates PaymentMethodType")
	@IOperation(Name = "createPaymentMethodType", Description = "Creates PaymentMethodType")
	public PaymentMethodType createPaymentMethodType(
			@HeaderParam("authenticationKey") String authenticationKey,
			PaymentMethodTypeCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createPaymentMethodType(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updatePaymentMethodType")
	@Operation(summary = "updatePaymentMethodType", description = "Updates PaymentMethodType")
	@IOperation(Name = "updatePaymentMethodType", Description = "Updates PaymentMethodType")
	public PaymentMethodType updatePaymentMethodType(
			@HeaderParam("authenticationKey") String authenticationKey,
			PaymentMethodTypeUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		PaymentMethodType paymentMethodType = service.getByIdOrNull(updateContainer.getId(),
				PaymentMethodType.class, null, securityContext);
		if (paymentMethodType == null) {
			throw new BadRequestException("no PaymentMethodType with id "
					+ updateContainer.getId());
		}
		updateContainer.setPaymentMethodType(paymentMethodType);

		return service.updatePaymentMethodType(updateContainer, securityContext);
	}
}