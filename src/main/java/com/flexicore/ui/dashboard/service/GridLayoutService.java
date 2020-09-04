package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.dashboard.data.GridLayoutRepository;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.request.GridLayoutCreate;
import com.flexicore.ui.dashboard.request.GridLayoutFiltering;
import com.flexicore.ui.dashboard.request.GridLayoutUpdate;
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
public class GridLayoutService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(GridLayoutService.class);

	@PluginInfo(version = 1)
	@Autowired
	private GridLayoutRepository gridLayoutRepository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public GridLayout updateGridLayout(GridLayoutUpdate gridLayoutUpdate, SecurityContext securityContext) {
		if (GridLayoutUpdateNoMerge(gridLayoutUpdate,
				gridLayoutUpdate.getGridLayout())) {
			gridLayoutRepository.merge(gridLayoutUpdate.getGridLayout());
		}
		return gridLayoutUpdate.getGridLayout();
	}

	public boolean GridLayoutUpdateNoMerge(GridLayoutCreate gridLayoutCreate, GridLayout gridLayout) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(gridLayoutCreate, gridLayout);

		return update;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return gridLayoutRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<GridLayout> listAllGridLayout(
			GridLayoutFiltering gridLayoutFiltering,
			SecurityContext securityContext) {
		return gridLayoutRepository.listAllGridLayout(gridLayoutFiltering,
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
		GridLayout gridLayout = new GridLayout(createGridLayout.getName(), securityContext);
		GridLayoutUpdateNoMerge(createGridLayout, gridLayout);
		return gridLayout;
	}

	public PaginationResponse<GridLayout> getAllGridLayout(
			GridLayoutFiltering gridLayoutFiltering,
			SecurityContext securityContext) {
		List<GridLayout> list = listAllGridLayout(gridLayoutFiltering,
				securityContext);
		long count = gridLayoutRepository.countAllGridLayout(
				gridLayoutFiltering, securityContext);
		return new PaginationResponse<>(list, gridLayoutFiltering, count);
	}

	public void validate(GridLayoutCreate createGridLayout,
			SecurityContext securityContext) {
		baseclassNewService.validate(createGridLayout, securityContext);

	}

	public void validate(GridLayoutFiltering gridLayoutFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(gridLayoutFiltering, securityContext);

	}

}
