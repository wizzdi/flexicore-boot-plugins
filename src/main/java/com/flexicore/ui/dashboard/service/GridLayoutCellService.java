package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.ui.dashboard.data.GridLayoutCellRepository;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.model.GridLayoutCell;
import com.flexicore.ui.dashboard.request.GridLayoutCellCreate;
import com.flexicore.ui.dashboard.request.GridLayoutCellFilter;
import com.flexicore.ui.dashboard.request.GridLayoutCellUpdate;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component
public class GridLayoutCellService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(GridLayoutCellService.class);

	
	@Autowired
	private GridLayoutCellRepository gridLayoutCellRepository;

	@Autowired
	private BasicService  baseclassNewService;

	public GridLayoutCell updateGridLayoutCell(GridLayoutCellUpdate gridLayoutCellUpdate, SecurityContextBase securityContext) {
		if (GridLayoutCellUpdateNoMerge(gridLayoutCellUpdate,
				gridLayoutCellUpdate.getGridLayoutCell())) {
			gridLayoutCellRepository.merge(gridLayoutCellUpdate.getGridLayoutCell());
		}
		return gridLayoutCellUpdate.getGridLayoutCell();
	}

	public boolean GridLayoutCellUpdateNoMerge(GridLayoutCellCreate gridLayoutCellCreate, GridLayoutCell gridLayoutCell) {
		boolean update = baseclassNewService.updateBasicNoMerge(gridLayoutCellCreate, gridLayoutCell);

		if(gridLayoutCellCreate.getGridLayout()!=null&&(gridLayoutCell.getGridLayout()==null||!gridLayoutCellCreate.getGridLayout().getId().equals(gridLayoutCell.getGridLayout().getId()))){
			gridLayoutCell.setGridLayout(gridLayoutCellCreate.getGridLayout());
			update=true;
		}
		Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(gridLayoutCellCreate.any(), gridLayoutCell.any());
		if (map != null) {
			gridLayoutCell.setJsonNode(map);
			update = true;
		}

		return update;
	}


	public List<GridLayoutCell> listAllGridLayoutCell(
			GridLayoutCellFilter gridLayoutCellFilter,
			SecurityContextBase securityContext) {
		return gridLayoutCellRepository.listAllGridLayoutCell(gridLayoutCellFilter,
				securityContext);
	}

	public GridLayoutCell createGridLayoutCell(GridLayoutCellCreate createGridLayoutCell,
			SecurityContextBase securityContext) {
		GridLayoutCell gridLayoutCell = createGridLayoutCellNoMerge(createGridLayoutCell,
				securityContext);
		gridLayoutCellRepository.merge(gridLayoutCell);
		return gridLayoutCell;

	}

	public GridLayoutCell createGridLayoutCellNoMerge(
			GridLayoutCellCreate createGridLayoutCell, SecurityContextBase securityContext) {
		GridLayoutCell gridLayoutCell = new GridLayoutCell();
		gridLayoutCell.setId(UUID.randomUUID().toString());
		GridLayoutCellUpdateNoMerge(createGridLayoutCell, gridLayoutCell);
		BaseclassService.createSecurityObjectNoMerge(gridLayoutCell,securityContext);
		return gridLayoutCell;
	}

	public PaginationResponse<GridLayoutCell> getAllGridLayoutCell(
			GridLayoutCellFilter gridLayoutCellFilter,
			SecurityContextBase securityContext) {
		List<GridLayoutCell> list = listAllGridLayoutCell(gridLayoutCellFilter,
				securityContext);
		long count = gridLayoutCellRepository.countAllGridLayoutCell(
				gridLayoutCellFilter, securityContext);
		return new PaginationResponse<>(list, gridLayoutCellFilter, count);
	}

	public void validate(GridLayoutCellCreate createGridLayoutCell,
			SecurityContextBase securityContext) {
		baseclassNewService.validate(createGridLayoutCell, securityContext);
		String gridLayoutId=createGridLayoutCell.getGridLayoutId();
		GridLayout dynamicExecution=gridLayoutId!=null?getByIdOrNull(gridLayoutId, GridLayout.class,null,securityContext):null;
		if(dynamicExecution==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No GridLayout with id "+gridLayoutId);
		}
		createGridLayoutCell.setGridLayout(dynamicExecution);

	}

	public void validate(GridLayoutCellFilter gridLayoutCellFilter,
						 SecurityContextBase securityContext) {
		baseclassNewService.validate(gridLayoutCellFilter, securityContext);

		Set<String> gridLayoutIds= gridLayoutCellFilter.getGridLayoutIds();
		Map<String, GridLayout> dashboardPresetMap=gridLayoutIds.isEmpty()?new HashMap<>():gridLayoutCellRepository.listByIds(GridLayout.class,gridLayoutIds, SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		gridLayoutIds.removeAll(dashboardPresetMap.keySet());
		if(!gridLayoutIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No GridLayout with ids "+gridLayoutIds);
		}
		gridLayoutCellFilter.setGridLayouts(new ArrayList<>(dashboardPresetMap.values()));

	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return gridLayoutCellRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return gridLayoutCellRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return gridLayoutCellRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return gridLayoutCellRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return gridLayoutCellRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return gridLayoutCellRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return gridLayoutCellRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		gridLayoutCellRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		gridLayoutCellRepository.massMerge(toMerge);
	}
}
