package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Basic;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.ui.dashboard.data.GridLayoutRepository;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.request.GridLayoutCreate;
import com.flexicore.ui.dashboard.request.GridLayoutFilter;
import com.flexicore.ui.dashboard.request.GridLayoutUpdate;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;


@Extension
@Component
public class GridLayoutService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(GridLayoutService.class);

	
	@Autowired
	private GridLayoutRepository gridLayoutRepository;

	@Autowired
	private BasicService  baseclassNewService;

	public GridLayout updateGridLayout(GridLayoutUpdate gridLayoutUpdate, SecurityContext securityContext) {
		if (GridLayoutUpdateNoMerge(gridLayoutUpdate,
				gridLayoutUpdate.getGridLayout())) {
			gridLayoutRepository.merge(gridLayoutUpdate.getGridLayout());
		}
		return gridLayoutUpdate.getGridLayout();
	}

	public boolean GridLayoutUpdateNoMerge(GridLayoutCreate gridLayoutCreate, GridLayout gridLayout) {
		boolean update = baseclassNewService.updateBasicNoMerge(gridLayoutCreate, gridLayout);
		Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(gridLayoutCreate.any(), gridLayout.any());
		if (map != null) {
			gridLayout.setJsonNode(map);
			update = true;
		}

		return update;
	}


	public List<GridLayout> listAllGridLayout(
			GridLayoutFilter gridLayoutFilter,
			SecurityContext securityContext) {
		return gridLayoutRepository.listAllGridLayout(gridLayoutFilter,
				securityContext);
	}

	public GridLayout createGridLayout(GridLayoutCreate createGridLayout,
			SecurityContext securityContext) {
		GridLayout gridLayout = createGridLayoutNoMerge(createGridLayout,
				securityContext);
		gridLayoutRepository.merge(gridLayout);
		return gridLayout;

	}

	public GridLayout createGridLayoutNoMerge(
			GridLayoutCreate createGridLayout, SecurityContext securityContext) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.setId(UUID.randomUUID().toString());
		GridLayoutUpdateNoMerge(createGridLayout, gridLayout);
		BaseclassService.createSecurityObjectNoMerge(gridLayout,securityContext);
		return gridLayout;
	}

	public PaginationResponse<GridLayout> getAllGridLayout(
			GridLayoutFilter gridLayoutFilter,
			SecurityContext securityContext) {
		List<GridLayout> list = listAllGridLayout(gridLayoutFilter,
				securityContext);
		long count = gridLayoutRepository.countAllGridLayout(
				gridLayoutFilter, securityContext);
		return new PaginationResponse<>(list, gridLayoutFilter, count);
	}

	public void validate(GridLayoutCreate createGridLayout,
			SecurityContext securityContext) {
		baseclassNewService.validate(createGridLayout, securityContext);

	}

	public void validate(GridLayoutFilter gridLayoutFilter,
						 SecurityContext securityContext) {
		baseclassNewService.validate(gridLayoutFilter, securityContext);

	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return gridLayoutRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return gridLayoutRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return gridLayoutRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return gridLayoutRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return gridLayoutRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return gridLayoutRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return gridLayoutRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		gridLayoutRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		gridLayoutRepository.massMerge(toMerge);
	}
}
