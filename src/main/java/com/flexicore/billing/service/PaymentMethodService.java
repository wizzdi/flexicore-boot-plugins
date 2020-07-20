package com.flexicore.billing.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.billing.data.PaymentMethodRepository;
import com.flexicore.billing.model.ContractItem;
import com.flexicore.billing.model.PaymentMethod;
import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.billing.request.PaymentMethodCreate;
import com.flexicore.billing.request.PaymentMethodFiltering;
import com.flexicore.billing.request.PaymentMethodUpdate;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Customer;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
@Primary
public class PaymentMethodService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PaymentMethodRepository repository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
                                                 List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
                                                   SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public void validateFiltering(PaymentMethodFiltering filtering,
								  SecurityContext securityContext) {
		baseclassNewService.validateFilter(filtering, securityContext);

		Set<String> customerIds = filtering.getCustomerIds();
		Map<String, Customer> customerMap = customerIds.isEmpty() ? new HashMap<>() : listByIds(Customer.class, customerIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		customerIds.removeAll(customerMap.keySet());
		if (!customerIds.isEmpty()) {
			throw new BadRequestException("No Customer with ids " + customerIds);
		}
		filtering.setCustomers(new ArrayList<>(customerMap.values()));

		Set<String> paymentMethodTypeIds = filtering.getPaymentMethodTypeIds();
		Map<String, PaymentMethodType> paymentMethodTypeMap = paymentMethodTypeIds.isEmpty() ? new HashMap<>() : listByIds(PaymentMethodType.class, paymentMethodTypeIds, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		paymentMethodTypeIds.removeAll(paymentMethodTypeMap.keySet());
		if (!paymentMethodTypeIds.isEmpty()) {
			throw new BadRequestException("No PaymentMethodType with ids " + paymentMethodTypeIds);
		}
		filtering.setPaymentMethodTypes(new ArrayList<>(paymentMethodTypeMap.values()));
	}

	public PaginationResponse<PaymentMethod> getAllPaymentMethods(
			SecurityContext securityContext, PaymentMethodFiltering filtering) {
		List<PaymentMethod> list = repository.getAllPaymentMethods(securityContext, filtering);
		long count = repository.countAllPaymentMethods(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public PaymentMethod createPaymentMethod(PaymentMethodCreate creationContainer,
												 SecurityContext securityContext) {
		PaymentMethod paymentMethod = createPaymentMethodNoMerge(creationContainer, securityContext);
		repository.merge(paymentMethod);
		return paymentMethod;
	}

	private PaymentMethod createPaymentMethodNoMerge(PaymentMethodCreate creationContainer,
                                       SecurityContext securityContext) {
		PaymentMethod paymentMethod = new PaymentMethod(creationContainer.getName(),securityContext);
		updatePaymentMethodNoMerge(paymentMethod, creationContainer);
		return paymentMethod;
	}

	private boolean updatePaymentMethodNoMerge(PaymentMethod paymentMethod,
			PaymentMethodCreate creationContainer) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(creationContainer, paymentMethod);
		if (creationContainer.getCustomer() != null && (paymentMethod.getCustomer() == null || !creationContainer.getCustomer().getId().equals(paymentMethod.getCustomer().getId()))) {
			paymentMethod.setCustomer(creationContainer.getCustomer());
			update = true;
		}
		if (creationContainer.getPaymentMethodType() != null && (paymentMethod.getPaymentMethodType() == null || !creationContainer.getPaymentMethodType().getId().equals(paymentMethod.getPaymentMethodType().getId()))) {
			paymentMethod.setPaymentMethodType(creationContainer.getPaymentMethodType());
			update = true;
		}

		if (creationContainer.getActive() != null && !creationContainer.getActive().equals(paymentMethod.isActive())) {
			paymentMethod.setActive(creationContainer.getActive());
			update = true;
		}
		return update;
	}

	public PaymentMethod updatePaymentMethod(PaymentMethodUpdate updateContainer,
												 SecurityContext securityContext) {
		PaymentMethod paymentMethod = updateContainer.getPaymentMethod();
		if (updatePaymentMethodNoMerge(paymentMethod, updateContainer)) {
			repository.merge(paymentMethod);
		}
		return paymentMethod;
	}

	public void validate(PaymentMethodCreate creationContainer,
                         SecurityContext securityContext) {
		baseclassNewService.validate(creationContainer, securityContext);
		String customerId = creationContainer.getCustomerId();
		Customer customer = customerId == null ? null : getByIdOrNull(customerId, Customer.class, null, securityContext);
		if (customer == null && customerId != null) {
			throw new BadRequestException("No Customer with id " + customerId);
		}
		creationContainer.setCustomer(customer);

		String paymentMethodTypeId = creationContainer.getPaymentMethodTypeId();
		PaymentMethodType paymentMethodType = paymentMethodTypeId == null ? null : getByIdOrNull(paymentMethodTypeId, PaymentMethodType.class, null, securityContext);
		if (paymentMethodType == null && paymentMethodTypeId != null) {
			throw new BadRequestException("No PaymentMethodType with id " + paymentMethodTypeId);
		}
		creationContainer.setPaymentMethodType(paymentMethodType);


	}
}