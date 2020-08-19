package com.flexicore.billing.interfaces;

import com.flexicore.billing.request.PayRequest;
import com.flexicore.billing.response.PayResponse;

public interface PaymentMethodService {

    PayResponse pay(PayRequest payRequest);
}
