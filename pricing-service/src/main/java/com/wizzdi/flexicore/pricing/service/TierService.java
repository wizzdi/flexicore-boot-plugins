package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.TierRepository;
import com.wizzdi.flexicore.pricing.model.price.*;
import com.wizzdi.flexicore.pricing.request.TierCreate;
import com.wizzdi.flexicore.pricing.request.TierFiltering;
import com.wizzdi.flexicore.pricing.request.TierUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component

public class TierService implements Plugin {

		@Autowired
	private TierRepository repository;

	@Autowired
	private BasicService basicService;



	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}


	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return repository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return repository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return repository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return repository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		repository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		repository.massMerge(toMerge);
	}

	public void validateFiltering(TierFiltering filtering,
                                  SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> pricingSchemesIds=filtering.getPricingSchemesIds();
		Map<String, PricingScheme> pricingSchemeMap=pricingSchemesIds.isEmpty()?new HashMap<>():listByIds(PricingScheme.class,pricingSchemesIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		pricingSchemesIds.removeAll(pricingSchemeMap.keySet());
		if(!pricingSchemesIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no PricingSchemes with ids "+pricingSchemesIds);
		}
		filtering.setPricingSchemes(new ArrayList<>(pricingSchemeMap.values()));
	}

	public PaginationResponse<Tier> getAllTier(
			SecurityContext securityContext, TierFiltering filtering) {
		List<Tier> list = listAllTier(securityContext, filtering);
		long count = repository.countAllTier(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public List<Tier> listAllTier(SecurityContext securityContext, TierFiltering filtering) {
		return repository.getAllTier(securityContext, filtering);
	}

	public Tier createTier(TierCreate creationContainer,
                                   SecurityContext securityContext) {
		Tier tier = createTierNoMerge(creationContainer, securityContext);
		repository.merge(tier);
		return tier;
	}

	private Tier createTierNoMerge(TierCreate creationContainer,
                                       SecurityContext securityContext) {
		Tier tier = new Tier();
		tier.setId(UUID.randomUUID().toString());

		updateTierNoMerge(tier, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(tier,securityContext);
		return tier;
	}

	private boolean updateTierNoMerge(Tier tier,
			TierCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, tier);
		if(creationContainer.getMoney()!=null&&(tier.getMoney()==null||!creationContainer.getMoney().getId().equals(tier.getMoney().getId()))){
			tier.setMoney(creationContainer.getMoney());
			update=true;
		}
		if(creationContainer.getPricingScheme()!=null&&(tier.getPricingScheme()==null||!creationContainer.getPricingScheme().getId().equals(tier.getPricingScheme().getId()))){
			tier.setPricingScheme(creationContainer.getPricingScheme());
			update=true;
		}
		if(creationContainer.getEndingQuantity()!=null&&!creationContainer.getEndingQuantity().equals(tier.getEndingQuantity())){
			tier.setEndingQuantity(creationContainer.getEndingQuantity());
			update=true;
		}
		if(creationContainer.getStartingQuantity()!=null&&!creationContainer.getStartingQuantity().equals(tier.getStartingQuantity())){
			tier.setStartingQuantity(creationContainer.getStartingQuantity());
			update=true;
		}
		return update;
	}

	public Tier updateTier(TierUpdate updateContainer,
                                   SecurityContext securityContext) {
		Tier tier = updateContainer.getTier();
		if (updateTierNoMerge(tier, updateContainer)) {
			repository.merge(tier);
		}
		return tier;
	}

	public void validate(TierCreate creationContainer,
                         SecurityContext securityContext) {
		basicService.validate(creationContainer, securityContext);
		String moneyId=creationContainer.getMoneyId();
		Money money=moneyId==null?null:getByIdOrNull(moneyId,Money.class, securityContext);
		if(money==null&&moneyId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no money with id "+moneyId);
		}
		creationContainer.setMoney(money);

		String pricingSchemeId=creationContainer.getPricingSchemeId();
		PricingScheme pricingScheme=pricingSchemeId==null?null:getByIdOrNull(pricingSchemeId,PricingScheme.class, securityContext);
		if(pricingScheme==null&&pricingSchemeId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no PricingScheme with id "+pricingSchemeId);
		}
		creationContainer.setPricingScheme(pricingScheme);

	}
}
