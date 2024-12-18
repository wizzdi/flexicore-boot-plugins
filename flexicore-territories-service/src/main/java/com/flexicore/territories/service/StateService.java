package com.flexicore.territories.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Country_;
import com.flexicore.model.territories.State;
import com.flexicore.model.territories.State_;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.territories.data.StateRepository;
import com.flexicore.territories.request.StateCreate;
import com.flexicore.territories.request.StateFilter;
import com.flexicore.territories.request.StateUpdate;
import com.wizzdi.flexicore.boot.base.annotations.plugins.PluginInfo;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class StateService implements Plugin {

	@PluginInfo(version = 1)
	@Autowired
	private StateRepository repository;

	@Autowired
	private BasicService basicService;

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}


	
	public List<State> listAllStates(SecurityContext SecurityContext,
			StateFilter filtering) {
		return repository.listAllStates(SecurityContext, filtering);

	}

	
	public PaginationResponse<State> getAllStates(
			SecurityContext SecurityContext, StateFilter filtering) {
		List<State> list = repository.listAllStates(SecurityContext, filtering);
		long count = repository.countAllStates(SecurityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	
	public State updateState(StateUpdate updateContainer,
			SecurityContext SecurityContext) {
		State state = updateContainer.getState();
		if (updateStateNoMerge(state, updateContainer)) {
			repository.merge(state);
		}
		return state;
	}

	public void validate(StateCreate stateCreate,
			SecurityContext SecurityContext) {
		String countryId = stateCreate.getCountryId();
		Country country = countryId != null ? getByIdOrNull(countryId, Country.class,SecurityContext) : null;
		if (country == null && countryId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Country with id " + countryId);
		}
		stateCreate.setCountry(country);
	}

	public void validate(StateFilter stateFiltering,
						 SecurityContext SecurityContext) {
		basicService.validate(stateFiltering, SecurityContext);
		Set<String> countriesIds = stateFiltering.getCountriesIds();
		Map<String, Country> countryMap = countriesIds.isEmpty()?new HashMap<>():repository.listByIds(Country.class,countriesIds, SecurityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		countriesIds.removeAll(countryMap.keySet());
		if(!countriesIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Countries with ids "+countriesIds);
		}
		stateFiltering.setCountries(new ArrayList<>(countryMap.values()));

	}

	public State createStateNoMerge(StateCreate stateCreate,
			SecurityContext SecurityContext) {
		State state = new State().setId(UUID.randomUUID().toString());
		BaseclassService.createSecurityObjectNoMerge(state,SecurityContext);
		updateStateNoMerge(state, stateCreate);
		return state;

	}

	public boolean updateStateNoMerge(State state, StateCreate stateCreate) {

		boolean update = basicService.updateBasicNoMerge(stateCreate, state);
		if (state.isSoftDelete()) {
			state.setSoftDelete(false);
			update = true;
		}
		if (stateCreate.getExternalId() != null && !stateCreate.getExternalId().equals(state.getExternalId())) {
			state.setExternalId(stateCreate.getExternalId());
			update = true;
		}
		if (stateCreate.getCountry() != null && (state.getCountry() == null || !stateCreate.getCountry().getId().equals(state.getCountry().getId()))) {
			state.setCountry(stateCreate.getCountry());
			update = true;
		}
		return update;

	}

	
	public State createState(StateCreate creationContainer,
			SecurityContext SecurityContext) {
		State state = createStateNoMerge(creationContainer, SecurityContext);
		repository.merge(state);
		return state;
	}

	
	public void deleteState(String stateid, SecurityContext SecurityContext) {
		State state = getByIdOrNull(stateid, State.class,SecurityContext);
		repository.remove(state);
	}
}
