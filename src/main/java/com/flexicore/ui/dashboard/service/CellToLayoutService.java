package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.ui.dashboard.data.CellToLayoutRepository;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.CellToLayoutCreate;
import com.flexicore.ui.dashboard.request.CellToLayoutFilter;
import com.flexicore.ui.dashboard.request.CellToLayoutUpdate;
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
public class CellToLayoutService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(CellToLayoutService.class);


	@Autowired
	private CellToLayoutRepository cellToLayoutRepository;

	@Autowired
	private BasicService  baseclassNewService;

	public CellToLayout updateCellToLayout(CellToLayoutUpdate cellToLayoutUpdate, SecurityContextBase securityContext) {
		if (CellToLayoutUpdateNoMerge(cellToLayoutUpdate,
				cellToLayoutUpdate.getCellToLayout())) {
			cellToLayoutRepository.merge(cellToLayoutUpdate.getCellToLayout());
		}
		return cellToLayoutUpdate.getCellToLayout();
	}

	public boolean CellToLayoutUpdateNoMerge(CellToLayoutCreate cellToLayoutCreate, CellToLayout cellToLayout) {
		boolean update = baseclassNewService.updateBasicNoMerge(cellToLayoutCreate, cellToLayout);
		if(cellToLayoutCreate.getCellContent()!=null&&(cellToLayout.getCellContent()==null||!cellToLayoutCreate.getCellContent().getId().equals(cellToLayout.getCellContent().getId()))){
			cellToLayout.setCellContent(cellToLayoutCreate.getCellContent());
			update=true;
		}
		if(cellToLayoutCreate.getDashboardPreset()!=null&&(cellToLayout.getDashboardPreset()==null||!cellToLayoutCreate.getDashboardPreset().getId().equals(cellToLayout.getDashboardPreset().getId()))){
			cellToLayout.setDashboardPreset(cellToLayoutCreate.getDashboardPreset());
			update=true;
		}


		if(cellToLayoutCreate.getGridLayoutCell()!=null&&(cellToLayout.getGridLayoutCell()==null||!cellToLayoutCreate.getGridLayoutCell().getId().equals(cellToLayout.getGridLayoutCell().getId()))){
			cellToLayout.setGridLayoutCell(cellToLayoutCreate.getGridLayoutCell());
			update=true;
		}
		if(cellToLayoutCreate.getListFieldPath()!=null&&!cellToLayoutCreate.getListFieldPath().equals(cellToLayout.getListFieldPath())){
			cellToLayout.setListFieldPath(cellToLayoutCreate.getListFieldPath());
			update=true;
		}

		return update;
	}


	public List<CellToLayout> listAllCellToLayout(
			CellToLayoutFilter cellToLayoutFilter,
			SecurityContextBase securityContext) {
		return cellToLayoutRepository.listAllCellToLayout(cellToLayoutFilter,
				securityContext);
	}

	public CellToLayout createCellToLayout(CellToLayoutCreate createCellToLayout,
			SecurityContextBase securityContext) {
		CellToLayout cellToLayout = createCellToLayoutNoMerge(createCellToLayout,
				securityContext);
		cellToLayoutRepository.merge(cellToLayout);
		return cellToLayout;

	}

	public CellToLayout createCellToLayoutNoMerge(
			CellToLayoutCreate createCellToLayout, SecurityContextBase securityContext) {
		CellToLayout cellToLayout = new CellToLayout();
		cellToLayout.setId(UUID.randomUUID().toString());
		CellToLayoutUpdateNoMerge(createCellToLayout, cellToLayout);
		BaseclassService.createSecurityObjectNoMerge(cellToLayout,securityContext);
		return cellToLayout;
	}

	public PaginationResponse<CellToLayout> getAllCellToLayout(
			CellToLayoutFilter cellToLayoutFilter,
			SecurityContextBase securityContext) {
		List<CellToLayout> list = listAllCellToLayout(cellToLayoutFilter,
				securityContext);
		long count = cellToLayoutRepository.countAllCellToLayout(
				cellToLayoutFilter, securityContext);
		return new PaginationResponse<>(list, cellToLayoutFilter, count);
	}

	public void validate(CellToLayoutCreate createCellToLayout,
			SecurityContextBase securityContext) {
		baseclassNewService.validate(createCellToLayout, securityContext);
		String dashboardPresetId=createCellToLayout.getDashboardPresetId();
		DashboardPreset dashboardPreset=dashboardPresetId!=null?getByIdOrNull(dashboardPresetId,DashboardPreset.class,null,securityContext):null;
		if(dashboardPreset==null&&dashboardPresetId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No DashboardPreset with id "+dashboardPresetId);
		}
		createCellToLayout.setDashboardPreset(dashboardPreset);

		String cellContentId=createCellToLayout.getCellContentId();
		CellContent cellContent=cellContentId!=null?getByIdOrNull(cellContentId, CellContent.class,null,securityContext):null;
		if(cellContent==null&&cellContentId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellContent with id "+cellContentId);
		}
		createCellToLayout.setCellContent(cellContent);

		String gridLayoutCellId=createCellToLayout.getGridLayoutCellId();
		GridLayoutCell gridLayoutCell=gridLayoutCellId!=null?getByIdOrNull(gridLayoutCellId, GridLayoutCell.class,null,securityContext):null;
		if(gridLayoutCell==null&&gridLayoutCellId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No GridLayoutCell with id "+gridLayoutCellId);
		}
		createCellToLayout.setGridLayoutCell(gridLayoutCell);

	}

	public void validate(CellToLayoutFilter cellToLayoutFilter,
						 SecurityContextBase securityContext) {
		baseclassNewService.validate(cellToLayoutFilter, securityContext);

		Set<String> dashboardPresetIds= cellToLayoutFilter.getDashboardPresetIds();
		Map<String,DashboardPreset> dashboardPresetMap=dashboardPresetIds.isEmpty()?new HashMap<>():cellToLayoutRepository.listByIds(DashboardPreset.class,dashboardPresetIds, SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		dashboardPresetIds.removeAll(dashboardPresetMap.keySet());
		if(!dashboardPresetIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No DashboardPreset with ids "+dashboardPresetIds);
		}
		cellToLayoutFilter.setDashboardPresets(new ArrayList<>(dashboardPresetMap.values()));

		Set<String> cellContentIds= cellToLayoutFilter.getCellContentIds();
		Map<String,CellContent> cellContentMap=cellContentIds.isEmpty()?new HashMap<>():cellToLayoutRepository.listByIds(CellContent.class,cellContentIds,SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellContentIds.removeAll(cellContentMap.keySet());
		if(!cellContentIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellContent with ids "+cellContentIds);
		}
		cellToLayoutFilter.setCellContents(new ArrayList<>(cellContentMap.values()));

		Set<String> gridLayoutCellIds= cellToLayoutFilter.getGridLayoutCellIds();
		Map<String,GridLayoutCell> gridLayoutCellMap=gridLayoutCellIds.isEmpty()?new HashMap<>():cellToLayoutRepository.listByIds(GridLayoutCell.class,gridLayoutCellIds,SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		gridLayoutCellIds.removeAll(gridLayoutCellMap.keySet());
		if(!gridLayoutCellIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No GridLayoutCell with ids "+gridLayoutCellIds);
		}
		cellToLayoutFilter.setGridLayoutCells(new ArrayList<>(gridLayoutCellMap.values()));


	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return cellToLayoutRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return cellToLayoutRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return cellToLayoutRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return cellToLayoutRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return cellToLayoutRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return cellToLayoutRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return cellToLayoutRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		cellToLayoutRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		cellToLayoutRepository.massMerge(toMerge);
	}
}
