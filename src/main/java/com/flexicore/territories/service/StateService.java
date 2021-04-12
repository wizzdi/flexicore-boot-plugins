package com.flexicore.territories.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Country_;
import com.flexicore.model.territories.State;
import com.flexicore.model.territories.State_;
import com.flexicore.security.SecurityContextBase;
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

import javax.persistence.metamodel.SingularAttribute;
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

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	
	public List<State> listAllStates(SecurityContextBase securityContextBase,
			StateFilter filtering) {
		return repository.listAllStates(securityContextBase, filtering);

	}

	
	public PaginationResponse<State> getAllStates(
			SecurityContextBase securityContextBase, StateFilter filtering) {
		List<State> list = repository.listAllStates(securityContextBase, filtering);
		long count = repository.countAllStates(securityContextBase, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	
	public State updateState(StateUpdate updateContainer,
			SecurityContextBase securityContextBase) {
		State state = updateContainer.getState();
		if (updateStateNoMerge(state, updateContainer)) {
			repository.merge(state);
		}
		return state;
	}

	public void validate(StateCreate stateCreate,
			SecurityContextBase securityContextBase) {
		String countryId = stateCreate.getCountryId();
		Country country = countryId != null ? getByIdOrNull(countryId, Country.class, Country_.security, securityContextBase) : null;
		if (country == null && countryId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Country with id " + countryId);
		}
		stateCreate.setCountry(country);
	}

	public void validate(StateFilter stateFiltering,
						 SecurityContextBase securityContextBase) {
		basicService.validate(stateFiltering, securityContextBase);
		Set<String> countriesIds = stateFiltering.getCountriesIds();
		Map<String, Country> countryMap = countriesIds.isEmpty()?new HashMap<>():repository.listByIds(Country.class,countriesIds, Country_.security,securityContextBase).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		countriesIds.removeAll(countryMap.keySet());
		if(!countriesIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Countries with ids "+countriesIds);
		}
		stateFiltering.setCountries(new ArrayList<>(countryMap.values()));

	}

	public State createStateNoMerge(StateCreate stateCreate,
			SecurityContextBase securityContextBase) {
		State state = new State().setId(Baseclass.getBase64ID());
		BaseclassService.createSecurityObjectNoMerge(state,securityContextBase);
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
			SecurityContextBase securityContextBase) {
		State state = createStateNoMerge(creationContainer, securityContextBase);
		repository.merge(state);
		return state;
	}

	
	public void deleteState(String stateid, SecurityContextBase securityContextBase) {
		State state = getByIdOrNull(stateid, State.class, State_.security, securityContextBase);
		repository.remove(state);
	}
}