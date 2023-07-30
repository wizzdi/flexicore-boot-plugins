package com.wizzdi.flexicore.billing.app;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.security.SecurityPolicy;
import com.flexicore.model.territories.Address;
import com.flexicore.organization.model.Site;
import com.wizzdi.flexicore.billing.model.billing.Charge;
import com.wizzdi.flexicore.billing.model.payment.Payment;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.pricing.model.price.PriceList;
import com.wizzdi.flexicore.pricing.model.product.PricedProduct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class EntitiesConfig {

	@Bean
	public EntitiesHolder entitiesHolder(){
		return new EntitiesHolder(new HashSet<>(Arrays.asList(Baseclass.class, Basic.class, SecurityPolicy.class, FileResource.class, Site.class, Address.class, Charge.class, Payment.class, PriceList.class, PricedProduct.class)));
	}
}
