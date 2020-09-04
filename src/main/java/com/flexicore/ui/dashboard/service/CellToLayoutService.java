package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.dashboard.data.CellToLayoutRepository;
import com.flexicore.ui.dashboard.model.*;
import com.flexicore.ui.dashboard.request.CellToLayoutCreate;
import com.flexicore.ui.dashboard.request.CellToLayoutFiltering;
import com.flexicore.ui.dashboard.request.CellToLayoutUpdate;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class CellToLayoutService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(CellToLayoutService.class);

	@PluginInfo(version = 1)
	@Autowired
	private CellToLayoutRepository cellToLayoutRepository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public CellToLayout updateCellToLayout(CellToLayoutUpdate cellToLayoutUpdate, SecurityContext securityContext) {
		if (CellToLayoutUpdateNoMerge(cellToLayoutUpdate,
				cellToLayoutUpdate.getCellToLayout())) {
			cellToLayoutRepository.merge(cellToLayoutUpdate.getCellToLayout());
		}
		return cellToLayoutUpdate.getCellToLayout();
	}

	public boolean CellToLayoutUpdateNoMerge(CellToLayoutCreate cellToLayoutCreate, CellToLayout cellToLayout) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(cellToLayoutCreate, cellToLayout);
		if(cellToLayoutCreate.getCellContent()!=null&&(cellToLayout.getCellContent()==null||!cellToLayoutCreate.getCellContent().getId().equals(cellToLayout.getCellContent().getId()))){
			cellToLayout.setCellContent(cellToLayoutCreate.getCellContent());
			update=true;
		}
		if(cellToLayoutCreate.getDashboardPreset()!=null&&(cellToLayout.getDashboardPreset()==null||!cellToLayoutCreate.getDashboardPreset().getId().equals(cellToLayout.getDashboardPreset().getId()))){
			cellToLayout.setDashboardPreset(cellToLayoutCreate.getDashboardPreset());
			update=true;
		}

		if(cellToLayoutCreate.getDynamicExecution()!=null&&(cellToLayout.getDynamicExecution()==null||!cellToLayoutCreate.getDynamicExecution().getId().equals(cellToLayout.getDynamicExecution().getId()))){
			cellToLayout.setDynamicExecution(cellToLayoutCreate.getDynamicExecution());
			update=true;
		}

		if(cellToLayoutCreate.getGridLayoutCell()!=null&&(cellToLayout.getGridLayoutCell()==null||!cellToLayoutCreate.getGridLayoutCell().getId().equals(cellToLayout.getGridLayoutCell().getId()))){
			cellToLayout.setGridLayoutCell(cellToLayoutCreate.getGridLayoutCell());
			update=true;
		}

		return update;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return cellToLayoutRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<CellToLayout> listAllCellToLayout(
			CellToLayoutFiltering cellToLayoutFiltering,
			SecurityContext securityContext) {
		return cellToLayoutRepository.listAllCellToLayout(cellToLayoutFiltering,
				securityContext);
	}

	public CellToLayout createCellToLayout(CellToLayoutCreate createCellToLayout,
			SecurityContext securityContext) {
		CellToLayout cellToLayout = createCellToLayoutNoMerge(createCellToLayout,
				securityContext);
		cellToLayoutRepository.merge(cellToLayout);
		return cellToLayout;

	}

	public CellToLayout createCellToLayoutNoMerge(
			CellToLayoutCreate createCellToLayout, SecurityContext securityContext) {
		CellToLayout cellToLayout = new CellToLayout(createCellToLayout.getName(), securityContext);
		CellToLayoutUpdateNoMerge(createCellToLayout, cellToLayout);
		return cellToLayout;
	}

	public PaginationResponse<CellToLayout> getAllCellToLayout(
			CellToLayoutFiltering cellToLayoutFiltering,
			SecurityContext securityContext) {
		List<CellToLayout> list = listAllCellToLayout(cellToLayoutFiltering,
				securityContext);
		long count = cellToLayoutRepository.countAllCellToLayout(
				cellToLayoutFiltering, securityContext);
		return new PaginationResponse<>(list, cellToLayoutFiltering, count);
	}

	public void validate(CellToLayoutCreate createCellToLayout,
			SecurityContext securityContext) {
		baseclassNewService.validate(createCellToLayout, securityContext);
		String dashboardPresetId=createCellToLayout.getDashboardPresetId();
		DashboardPreset gridPreset=dashboardPresetId!=null?getByIdOrNull(dashboardPresetId,DashboardPreset.class,null,securityContext):null;
		if(gridPreset==null){
			throw new BadRequestException("No DashboardPreset with id "+dashboardPresetId);
		}
		createCellToLayout.setDashboardPreset(gridPreset);

		String cellContentId=createCellToLayout.getCellContentId();
		CellContent cellContent=cellContentId!=null?getByIdOrNull(cellContentId, CellContent.class,null,securityContext):null;
		if(cellContent==null){
			throw new BadRequestException("No CellContent with id "+cellContentId);
		}
		createCellToLayout.setCellContent(cellContent);

		String gridLayoutCellId=createCellToLayout.getGridLayoutCellId();
		GridLayoutCell gridLayoutCell=gridLayoutCellId!=null?getByIdOrNull(gridLayoutCellId, GridLayoutCell.class,null,securityContext):null;
		if(gridLayoutCell==null){
			throw new BadRequestException("No GridLayoutCell with id "+gridLayoutCellId);
		}
		createCellToLayout.setGridLayoutCell(gridLayoutCell);

		String dynamicExecutionId=createCellToLayout.getDynamicExecutionId();
		DynamicExecution dynamicExecution=dynamicExecutionId!=null?getByIdOrNull(dynamicExecutionId, DynamicExecution.class,null,securityContext):null;
		if(dynamicExecution==null){
			throw new BadRequestException("No DynamicExecution with id "+dynamicExecutionId);
		}
		createCellToLayout.setDynamicExecution(dynamicExecution);
	}

	public void validate(CellToLayoutFiltering cellToLayoutFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(cellToLayoutFiltering, securityContext);

		Set<String> dashboardPresetIds=cellToLayoutFiltering.getDashboardPresetIds();
		Map<String,DashboardPreset> dashboardPresetMap=dashboardPresetIds.isEmpty()?new HashMap<>():cellToLayoutRepository.listByIds(DashboardPreset.class,dashboardPresetIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		dashboardPresetIds.removeAll(dashboardPresetMap.keySet());
		if(!dashboardPresetIds.isEmpty()){
			throw new BadRequestException("No DashboardPreset with ids "+dashboardPresetIds);
		}
		cellToLayoutFiltering.setDashboardPresets(new ArrayList<>(dashboardPresetMap.values()));

		Set<String> cellContentIds=cellToLayoutFiltering.getCellContentIds();
		Map<String,CellContent> cellContentMap=cellContentIds.isEmpty()?new HashMap<>():cellToLayoutRepository.listByIds(CellContent.class,cellContentIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellContentIds.removeAll(cellContentMap.keySet());
		if(!cellContentIds.isEmpty()){
			throw new BadRequestException("No CellContent with ids "+cellContentIds);
		}
		cellToLayoutFiltering.setCellContents(new ArrayList<>(cellContentMap.values()));

		Set<String> gridLayoutCellIds=cellToLayoutFiltering.getGridLayoutCellIds();
		Map<String,GridLayoutCell> gridLayoutCellMap=gridLayoutCellIds.isEmpty()?new HashMap<>():cellToLayoutRepository.listByIds(GridLayoutCell.class,gridLayoutCellIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		gridLayoutCellIds.removeAll(gridLayoutCellMap.keySet());
		if(!gridLayoutCellIds.isEmpty()){
			throw new BadRequestException("No GridLayoutCell with ids "+gridLayoutCellIds);
		}
		cellToLayoutFiltering.setGridLayoutCells(new ArrayList<>(gridLayoutCellMap.values()));

		Set<String> dynamicExecutionIds=cellToLayoutFiltering.getDynamicExecutionIds();
		Map<String,DynamicExecution> dynamicExecutionMap=dynamicExecutionIds.isEmpty()?new HashMap<>():cellToLayoutRepository.listByIds(DynamicExecution.class,dynamicExecutionIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		dynamicExecutionIds.removeAll(dynamicExecutionMap.keySet());
		if(!dynamicExecutionIds.isEmpty()){
			throw new BadRequestException("No DynamicExecution with ids "+dynamicExecutionIds);
		}
		cellToLayoutFiltering.setDynamicExecutions(new ArrayList<>(dynamicExecutionMap.values()));

	}

}
