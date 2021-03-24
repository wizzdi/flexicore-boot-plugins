package com.flexicore.billing.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;


import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.billing.model.PaymentMethod_;
import com.flexicore.billing.request.PaymentMethodCreate;
import com.flexicore.billing.request.PaymentMethodFiltering;
import com.flexicore.billing.request.PaymentMethodUpdate;
import com.flexicore.billing.service.PaymentMethodService;
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

@RequestMapping("/plugins/PaymentMethod")

@Tag(name = "PaymentMethod")
@Extension
@RestController
public class PaymentMethodController implements Plugin {

		@Autowired
	private PaymentMethodService service;



	@Operation(summary = "getAllPaymentMethods", description = "Lists all PaymentMethods")
	@IOperation(Name = "getAllPaymentMethods", Description = "Lists all PaymentMethods")
	@PostMapping("/getAllPaymentMethods")
	public PaginationResponse<PaymentMethod> getAllPaymentMethods(

			@RequestBody PaymentMethodFiltering filtering, @RequestAttribute SecurityContextBase securityContext) {
		service.validateFiltering(filtering, securityContext);
		return service.getAllPaymentMethods(securityContext, filtering);
	}



	@PostMapping("/createPaymentMethod")
	@Operation(summary = "createPaymentMethod", description = "Creates PaymentMethod")
	@IOperation(Name = "createPaymentMethod", Description = "Creates PaymentMethod")
	public PaymentMethod createPaymentMethod(

			@RequestBody PaymentMethodCreate creationContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(creationContainer, securityContext);

		return service.createPaymentMethod(creationContainer, securityContext);
	}



	@PutMapping("/updatePaymentMethod")
	@Operation(summary = "updatePaymentMethod", description = "Updates PaymentMethod")
	@IOperation(Name = "updatePaymentMethod", Description = "Updates PaymentMethod")
	public PaymentMethod updatePaymentMethod(

			@RequestBody PaymentMethodUpdate updateContainer,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(updateContainer, securityContext);
		PaymentMethod paymentMethod = service.getByIdOrNull(updateContainer.getId(),
				PaymentMethod.class, PaymentMethod_.security, securityContext);
		if (paymentMethod == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no PaymentMethod with id "
					+ updateContainer.getId());
		}
		updateContainer.setPaymentMethod(paymentMethod);

		return service.updatePaymentMethod(updateContainer, securityContext);
	}
}