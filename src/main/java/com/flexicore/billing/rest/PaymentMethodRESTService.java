package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.billing.request.PaymentMethodCreate;
import com.flexicore.billing.request.PaymentMethodFiltering;
import com.flexicore.billing.request.PaymentMethodUpdate;
import com.flexicore.billing.service.PaymentMethodService;
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
@Path("plugins/PaymentMethod")
@RequestScoped
@Tag(name = "PaymentMethod")
@Extension
@Component
public class PaymentMethodRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PaymentMethodService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllPaymentMethods", description = "Lists all PaymentMethods")
	@IOperation(Name = "getAllPaymentMethods", Description = "Lists all PaymentMethods")
	@Path("getAllPaymentMethods")
	public PaginationResponse<PaymentMethod> getAllPaymentMethods(
			@HeaderParam("authenticationKey") String authenticationKey,
			PaymentMethodFiltering filtering, @Context SecurityContext securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllPaymentMethods(securityContext, filtering);
	}

	@POST
	@Produces("application/json")
	@Path("/createPaymentMethod")
	@Operation(summary = "createPaymentMethod", description = "Creates PaymentMethod")
	@IOperation(Name = "createPaymentMethod", Description = "Creates PaymentMethod")
	public PaymentMethod createPaymentMethod(
			@HeaderParam("authenticationKey") String authenticationKey,
			PaymentMethodCreate creationContainer,
			@Context SecurityContext securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createPaymentMethod(creationContainer, securityContext);
	}

	@POST
	@Produces("application/json")
	@Path("/updatePaymentMethod")
	@Operation(summary = "updatePaymentMethod", description = "Updates PaymentMethod")
	@IOperation(Name = "updatePaymentMethod", Description = "Updates PaymentMethod")
	public PaymentMethod updatePaymentMethod(
			@HeaderParam("authenticationKey") String authenticationKey,
			PaymentMethodUpdate updateContainer,
			@Context SecurityContext securityContext) {
		service.validate(updateContainer, securityContext);
		PaymentMethod paymentMethod = service.getByIdOrNull(updateContainer.getId(),
				PaymentMethod.class, null, securityContext);
		if (paymentMethod == null) {
			throw new BadRequestException("no PaymentMethod with id "
					+ updateContainer.getId());
		}
		updateContainer.setPaymentMethod(paymentMethod);

		return service.updatePaymentMethod(updateContainer, securityContext);
	}
}