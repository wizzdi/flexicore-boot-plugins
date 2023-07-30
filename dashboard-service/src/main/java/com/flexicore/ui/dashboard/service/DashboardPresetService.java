package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;

import com.flexicore.ui.dashboard.data.DashboardPresetRepository;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.request.DashboardPresetCreate;
import com.flexicore.ui.dashboard.request.DashboardPresetFilter;
import com.flexicore.ui.dashboard.request.DashboardPresetUpdate;
import com.flexicore.ui.dashboard.model.DashboardPreset;

import com.flexicore.ui.service.PresetService;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component
public class DashboardPresetService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(DashboardPresetService.class);

	
	@Autowired
	private DashboardPresetRepository dashboardPresetRepository;

	@Autowired
	
	private PresetService presetService;

	public DashboardPreset updateDashboardPreset(DashboardPresetUpdate dashboardPresetUpdate, SecurityContextBase securityContext) {
		if (DashboardPresetUpdateNoMerge(dashboardPresetUpdate,
				dashboardPresetUpdate.getDashboardPreset())) {
			dashboardPresetRepository.merge(dashboardPresetUpdate.getDashboardPreset());
		}
		return dashboardPresetUpdate.getDashboardPreset();
	}

	public boolean DashboardPresetUpdateNoMerge(DashboardPresetCreate dashboardPresetCreate, DashboardPreset dashboardPreset) {
		boolean update = presetService.updatePresetNoMerge(dashboardPresetCreate, dashboardPreset);

		if(dashboardPresetCreate.getGridLayout()!=null&&(dashboardPreset.getGridLayout()==null||!dashboardPresetCreate.getGridLayout().getId().equals(dashboardPreset.getGridLayout().getId()))){
			dashboardPreset.setGridLayout(dashboardPresetCreate.getGridLayout());
			update=true;
		}

		return update;
	}

	public List<DashboardPreset> listAllDashboardPreset(
			DashboardPresetFilter dashboardPresetFilter,
			SecurityContextBase securityContext) {
		return dashboardPresetRepository.listAllDashboardPreset(dashboardPresetFilter,
				securityContext);
	}

	public DashboardPreset createDashboardPreset(DashboardPresetCreate createDashboardPreset,
			SecurityContextBase securityContext) {
		DashboardPreset dashboardPreset = createDashboardPresetNoMerge(createDashboardPreset,
				securityContext);
		dashboardPresetRepository.merge(dashboardPreset);
		return dashboardPreset;

	}

	public DashboardPreset createDashboardPresetNoMerge(
			DashboardPresetCreate createDashboardPreset, SecurityContextBase securityContext) {
		DashboardPreset dashboardPreset = new DashboardPreset();
		dashboardPreset.setId(UUID.randomUUID().toString());
		DashboardPresetUpdateNoMerge(createDashboardPreset, dashboardPreset);
		BaseclassService.createSecurityObjectNoMerge(dashboardPreset,securityContext);
		return dashboardPreset;
	}

	public PaginationResponse<DashboardPreset> getAllDashboardPreset(
			DashboardPresetFilter dashboardPresetFilter,
			SecurityContextBase securityContext) {
		List<DashboardPreset> list = listAllDashboardPreset(dashboardPresetFilter,
				securityContext);
		long count = dashboardPresetRepository.countAllDashboardPreset(
				dashboardPresetFilter, securityContext);
		return new PaginationResponse<>(list, dashboardPresetFilter, count);
	}

	public void validate(DashboardPresetCreate createDashboardPreset,
			SecurityContextBase securityContext) {
		presetService.validate(createDashboardPreset, securityContext);
		String presetId=createDashboardPreset.getGridLayoutId();
		GridLayout gridPreset=presetId!=null?getByIdOrNull(presetId,GridLayout.class,SecuredBasic_.security,securityContext):null;
		if(gridPreset==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No GridLayout with id "+presetId);
		}
		createDashboardPreset.setGridLayout(gridPreset);
	}

	public void validate(DashboardPresetFilter dashboardPresetFilter,
						 SecurityContextBase securityContext) {
		presetService.validate(dashboardPresetFilter, securityContext);
		Set<String> presetIds= dashboardPresetFilter.getGridLayoutIds();
		Map<String,GridLayout> presetMap=presetIds.isEmpty()?new HashMap<>():dashboardPresetRepository.listByIds(GridLayout.class,presetIds, SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		presetIds.removeAll(presetMap.keySet());
		if(!presetIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No GridLayout with ids "+presetIds);
		}
		dashboardPresetFilter.setGridLayouts(new ArrayList<>(presetMap.values()));
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return dashboardPresetRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return dashboardPresetRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return dashboardPresetRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return dashboardPresetRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return dashboardPresetRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return dashboardPresetRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return dashboardPresetRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		dashboardPresetRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		dashboardPresetRepository.massMerge(toMerge);
	}
}
