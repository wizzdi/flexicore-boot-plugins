package com.wizzdi.flexicore.pricing.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.pricing.data.RecurringPriceEntryRepository;
import com.wizzdi.flexicore.pricing.model.price.*;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryCreate;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryFiltering;
import com.wizzdi.flexicore.pricing.request.RecurringPriceEntryUpdate;
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

public class RecurringPriceEntryService implements Plugin {

		@Autowired
	private RecurringPriceEntryRepository repository;

	@Autowired
	private BasicService basicService;

public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
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

	public void validateFiltering(RecurringPriceEntryFiltering filtering,
                                  SecurityContextBase securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> recurringPriceIds=filtering.getRecurringPriceIds();
		Map<String, RecurringPrice> recurringPriceMap=recurringPriceIds.isEmpty()?new HashMap<>():listByIds(RecurringPrice.class,recurringPriceIds,SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		recurringPriceIds.removeAll(recurringPriceMap.keySet());
		if(!recurringPriceIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no RecurringPrices with ids "+recurringPriceIds);
		}
		filtering.setRecurringPrices(new ArrayList<>(recurringPriceMap.values()));
	}

	public PaginationResponse<RecurringPriceEntry> getAllRecurringPriceEntry(
			SecurityContextBase securityContext, RecurringPriceEntryFiltering filtering) {
		List<RecurringPriceEntry> list = listAllRecurringPriceEntry(securityContext, filtering);
		long count = repository.countAllRecurringPriceEntry(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public List<RecurringPriceEntry> listAllRecurringPriceEntry(SecurityContextBase securityContext, RecurringPriceEntryFiltering filtering) {
		return repository.getAllRecurringPriceEntry(securityContext, filtering);
	}

	public RecurringPriceEntry createRecurringPriceEntry(RecurringPriceEntryCreate creationContainer,
                                   SecurityContextBase securityContext) {
		RecurringPriceEntry recurringPriceEntry = createRecurringPriceEntryNoMerge(creationContainer, securityContext);
		repository.merge(recurringPriceEntry);
		return recurringPriceEntry;
	}

	private RecurringPriceEntry createRecurringPriceEntryNoMerge(RecurringPriceEntryCreate creationContainer,
                                       SecurityContextBase securityContext) {
		RecurringPriceEntry recurringPriceEntry = new RecurringPriceEntry();
		recurringPriceEntry.setId(Baseclass.getBase64ID());

		updateRecurringPriceEntryNoMerge(recurringPriceEntry, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(recurringPriceEntry,securityContext);
		return recurringPriceEntry;
	}

	private boolean updateRecurringPriceEntryNoMerge(RecurringPriceEntry recurringPriceEntry,
			RecurringPriceEntryCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, recurringPriceEntry);

		if(creationContainer.getRecurringPrice()!=null&&(recurringPriceEntry.getRecurringPrice()==null||!creationContainer.getRecurringPrice().getId().equals(recurringPriceEntry.getRecurringPrice().getId()))){
			recurringPriceEntry.setRecurringPrice(creationContainer.getRecurringPrice());
			update=true;
		}
		if(creationContainer.getFrequency()!=null&&(recurringPriceEntry.getFrequency()==null||!creationContainer.getFrequency().getId().equals(recurringPriceEntry.getFrequency().getId()))){
			recurringPriceEntry.setFrequency(creationContainer.getFrequency());
			update=true;
		}
		if(creationContainer.getPricingScheme()!=null&&(recurringPriceEntry.getPricingScheme()==null||!creationContainer.getPricingScheme().getId().equals(recurringPriceEntry.getPricingScheme().getId()))){
			recurringPriceEntry.setPricingScheme(creationContainer.getPricingScheme());
			update=true;
		}
		if(creationContainer.getSequence()!=null&&!creationContainer.getSequence().equals(recurringPriceEntry.getSequence())){
			recurringPriceEntry.setSequence(creationContainer.getSequence());
			update=true;
		}
		if(creationContainer.getTenureType()!=null&&!creationContainer.getTenureType().equals(recurringPriceEntry.getTenureType())){
			recurringPriceEntry.setTenureType(creationContainer.getTenureType());
			update=true;
		}
		if(creationContainer.getTotalCycles()!=null&&!creationContainer.getTotalCycles().equals(recurringPriceEntry.getTotalCycles())){
			recurringPriceEntry.setTotalCycles(creationContainer.getTotalCycles());
			update=true;
		}
		return update;
	}

	public RecurringPriceEntry updateRecurringPriceEntry(RecurringPriceEntryUpdate updateContainer,
                                   SecurityContextBase securityContext) {
		RecurringPriceEntry recurringPriceEntry = updateContainer.getRecurringPriceEntry();
		if (updateRecurringPriceEntryNoMerge(recurringPriceEntry, updateContainer)) {
			repository.merge(recurringPriceEntry);
		}
		return recurringPriceEntry;
	}

	public void validate(RecurringPriceEntryCreate creationContainer,
                         SecurityContextBase securityContext) {
		basicService.validate(creationContainer, securityContext);
		String frequencyId=creationContainer.getFrequencyId();
		Frequency frequency=frequencyId==null?null:getByIdOrNull(frequencyId,Frequency.class, SecuredBasic_.security,securityContext);
		if(frequency==null&&frequencyId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Frequency with id "+frequencyId);
		}
		creationContainer.setFrequency(frequency);

		String recurringPriceId=creationContainer.getRecurringPriceId();
		RecurringPrice recurringPrice=recurringPriceId==null?null:getByIdOrNull(recurringPriceId,RecurringPrice.class, SecuredBasic_.security,securityContext);
		if(recurringPrice==null&&recurringPriceId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no RecurringPrice with id "+recurringPriceId);
		}
		creationContainer.setRecurringPrice(recurringPrice);

		String pricingSchemeId=creationContainer.getPricingSchemeId();
		PricingScheme pricingScheme=pricingSchemeId==null?null:getByIdOrNull(pricingSchemeId,PricingScheme.class, SecuredBasic_.security,securityContext);
		if(pricingScheme==null&&pricingSchemeId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no PricingScheme with id "+pricingSchemeId);
		}
		creationContainer.setPricingScheme(pricingScheme);


	}
}