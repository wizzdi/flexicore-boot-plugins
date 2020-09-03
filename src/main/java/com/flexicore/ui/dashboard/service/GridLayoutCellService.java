package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.dashboard.data.GridLayoutCellRepository;
import com.flexicore.ui.dashboard.model.DashboardPreset;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.model.GridLayoutCell;
import com.flexicore.ui.dashboard.request.GridLayoutCellCreate;
import com.flexicore.ui.dashboard.request.GridLayoutCellFiltering;
import com.flexicore.ui.dashboard.request.GridLayoutCellUpdate;
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
public class GridLayoutCellService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(GridLayoutCellService.class);

	@PluginInfo(version = 1)
	@Autowired
	private GridLayoutCellRepository gridLayoutCellRepository;

	@Autowired
	@PluginInfo(version = 1)
	private BaseclassNewService baseclassNewService;

	public GridLayoutCell updateGridLayoutCell(GridLayoutCellUpdate gridLayoutCellUpdate, SecurityContext securityContext) {
		if (GridLayoutCellUpdateNoMerge(gridLayoutCellUpdate,
				gridLayoutCellUpdate.getGridLayoutCell())) {
			gridLayoutCellRepository.merge(gridLayoutCellUpdate.getGridLayoutCell());
		}
		return gridLayoutCellUpdate.getGridLayoutCell();
	}

	public boolean GridLayoutCellUpdateNoMerge(GridLayoutCellCreate gridLayoutCellCreate, GridLayoutCell gridLayoutCell) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(gridLayoutCellCreate, gridLayoutCell);
		if(gridLayoutCellCreate.getContextString()!=null&&!gridLayoutCellCreate.getContextString().equals(gridLayoutCell.getContextString())){
			gridLayoutCell.setContextString(gridLayoutCellCreate.getContextString());
			update=true;
		}

		if(gridLayoutCellCreate.getGridLayout()!=null&&(gridLayoutCell.getGridLayout()==null||!gridLayoutCellCreate.getGridLayout().getId().equals(gridLayoutCell.getGridLayout().getId()))){
			gridLayoutCell.setGridLayout(gridLayoutCellCreate.getGridLayout());
			update=true;
		}

		return update;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return gridLayoutCellRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<GridLayoutCell> listAllGridLayoutCell(
			GridLayoutCellFiltering gridLayoutCellFiltering,
			SecurityContext securityContext) {
		return gridLayoutCellRepository.listAllGridLayoutCell(gridLayoutCellFiltering,
				securityContext);
	}

	public GridLayoutCell createGridLayoutCell(GridLayoutCellCreate createGridLayoutCell,
			SecurityContext securityContext) {
		GridLayoutCell gridLayoutCell = createGridLayoutCellNoMerge(createGridLayoutCell,
				securityContext);
		gridLayoutCellRepository.merge(gridLayoutCell);
		return gridLayoutCell;

	}

	public GridLayoutCell createGridLayoutCellNoMerge(
			GridLayoutCellCreate createGridLayoutCell, SecurityContext securityContext) {
		GridLayoutCell gridLayoutCell = new GridLayoutCell(createGridLayoutCell.getName(), securityContext);
		GridLayoutCellUpdateNoMerge(createGridLayoutCell, gridLayoutCell);
		return gridLayoutCell;
	}

	public PaginationResponse<GridLayoutCell> getAllGridLayoutCell(
			GridLayoutCellFiltering gridLayoutCellFiltering,
			SecurityContext securityContext) {
		List<GridLayoutCell> list = listAllGridLayoutCell(gridLayoutCellFiltering,
				securityContext);
		long count = gridLayoutCellRepository.countAllGridLayoutCell(
				gridLayoutCellFiltering, securityContext);
		return new PaginationResponse<>(list, gridLayoutCellFiltering, count);
	}

	public void validate(GridLayoutCellCreate createGridLayoutCell,
			SecurityContext securityContext) {
		baseclassNewService.validate(createGridLayoutCell, securityContext);
		String gridLayoutId=createGridLayoutCell.getGridLayoutId();
		GridLayout dynamicExecution=gridLayoutId!=null?getByIdOrNull(gridLayoutId, GridLayout.class,null,securityContext):null;
		if(dynamicExecution==null){
			throw new BadRequestException("No GridLayout with id "+gridLayoutId);
		}
		createGridLayoutCell.setGridLayout(dynamicExecution);

	}

	public void validate(GridLayoutCellFiltering gridLayoutCellFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(gridLayoutCellFiltering, securityContext);

		Set<String> gridLayoutIds=gridLayoutCellFiltering.getGridLayoutIds();
		Map<String, GridLayout> dashboardPresetMap=gridLayoutIds.isEmpty()?new HashMap<>():gridLayoutCellRepository.listByIds(GridLayout.class,gridLayoutIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		gridLayoutIds.removeAll(dashboardPresetMap.keySet());
		if(!gridLayoutIds.isEmpty()){
			throw new BadRequestException("No GridLayout with ids "+gridLayoutIds);
		}
		gridLayoutCellFiltering.setGridLayouts(new ArrayList<>(dashboardPresetMap.values()));

	}

}
