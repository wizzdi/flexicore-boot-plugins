package com.flexicore.billing.interfaces;

import com.flexicore.billing.model.PaymentMethodType;
import com.flexicore.billing.request.ActivateContractItemRequest;
import com.flexicore.billing.request.CreditCustomerRequest;
import com.flexicore.billing.response.ActivateContractItemResponse;
import com.flexicore.billing.response.CreditCustomerResponse;

public interface PaymentMethodService {

    ActivateContractItemResponse activateContractItem(ActivateContractItemRequest activateContractItemRequest);
    CreditCustomerResponse creditCustomer(CreditCustomerRequest creditCustomerRequest);

    boolean isType(PaymentMethodType paymentMethodType);
}
