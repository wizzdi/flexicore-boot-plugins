package com.flexicore.territories.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.State;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.territories.data.StateRepository;
import com.flexicore.territories.interfaces.IStateService;
import com.flexicore.territories.request.StateCreate;
import com.flexicore.territories.request.StateFiltering;
import com.flexicore.territories.request.StateUpdate;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.logging.Logger;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class StateService implements IStateService {

	@PluginInfo(version = 1)
	@Autowired
	private StateRepository repository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	@Autowired
	private Logger logger;

	@Override
	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batch, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batch, securityContext);
	}

	@Override
	public List<State> listAllStates(SecurityContext securityContext,
			StateFiltering filtering) {
		return repository.listAllStates(securityContext, filtering);

	}

	@Override
	public PaginationResponse<State> getAllStates(
			SecurityContext securityContext, StateFiltering filtering) {
		List<State> list = repository.listAllStates(securityContext, filtering);
		long count = repository.countAllStates(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public State updateState(StateUpdate updateContainer,
			SecurityContext securityContext) {
		State state = updateContainer.getState();
		if (updateStateNoMerge(state, updateContainer)) {
			repository.merge(state);
		}
		return state;
	}

	public void validate(StateCreate stateCreate,
			SecurityContext securityContext) {
		String countryId = stateCreate.getCountryId();
		Country country = countryId != null ? getByIdOrNull(countryId,
				Country.class, null, securityContext) : null;
		if (country == null && countryId != null) {
			throw new BadRequestException("no Country with id " + countryId);
		}
		stateCreate.setCountry(country);
	}

	public void validate(StateFiltering stateFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(stateFiltering, securityContext);
		Country country = stateFiltering.getCountryId() != null
				? getByIdOrNull(stateFiltering.getCountryId(), Country.class,
						null, securityContext) : null;
		if (country == null && stateFiltering.getCountryId() != null) {
			throw new BadRequestException("no Country with id "
					+ stateFiltering.getCountryId());
		}
		stateFiltering.setCountry(country);
	}

	public State createStateNoMerge(StateCreate stateCreate,
			SecurityContext securityContext) {
		State state = new State(stateCreate.getName(), securityContext);
		updateStateNoMerge(state, stateCreate);
		return state;

	}

	public boolean updateStateNoMerge(State state, StateCreate stateCreate) {

		boolean update = baseclassNewService.updateBaseclassNoMerge(
				stateCreate, state);
		if (state.isSoftDelete()) {
			state.setSoftDelete(false);
			update = true;
		}
		if (stateCreate.getExternalId() != null
				&& !stateCreate.getExternalId().equals(state.getExternalId())) {
			state.setExternalId(stateCreate.getExternalId());
			update = true;
		}
		if (stateCreate.getCountry() != null
				&& (state.getCountry() == null || !stateCreate.getCountry()
						.getId().equals(state.getCountry().getId()))) {
			state.setCountry(stateCreate.getCountry());
			update = true;
		}
		return update;

	}

	@Override
	public State createState(StateCreate creationContainer,
			SecurityContext securityContext) {
		State state = createStateNoMerge(creationContainer, securityContext);
		repository.merge(state);
		return state;
	}

	@Override
	public void deleteState(String stateid, SecurityContext securityContext) {
		State state = getByIdOrNull(stateid, State.class, null, securityContext);
		repository.remove(state);
	}
}