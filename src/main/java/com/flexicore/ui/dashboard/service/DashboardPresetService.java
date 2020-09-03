package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import com.flexicore.ui.dashboard.data.DashboardPresetRepository;
import com.flexicore.ui.dashboard.model.GridLayout;
import com.flexicore.ui.dashboard.request.DashboardPresetCreate;
import com.flexicore.ui.dashboard.request.DashboardPresetFiltering;
import com.flexicore.ui.dashboard.request.DashboardPresetUpdate;
import com.flexicore.ui.dashboard.model.DashboardPreset;

import com.flexicore.ui.service.PresetService;
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
public class DashboardPresetService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(DashboardPresetService.class);

	@PluginInfo(version = 1)
	@Autowired
	private DashboardPresetRepository dashboardPresetRepository;

	@Autowired
	@PluginInfo(version = 1)
	private PresetService presetService;

	public DashboardPreset updateDashboardPreset(DashboardPresetUpdate dashboardPresetUpdate, SecurityContext securityContext) {
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

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return dashboardPresetRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<DashboardPreset> listAllDashboardPreset(
			DashboardPresetFiltering dashboardPresetFiltering,
			SecurityContext securityContext) {
		return dashboardPresetRepository.listAllDashboardPreset(dashboardPresetFiltering,
				securityContext);
	}

	public DashboardPreset createDashboardPreset(DashboardPresetCreate createDashboardPreset,
			SecurityContext securityContext) {
		DashboardPreset dashboardPreset = createDashboardPresetNoMerge(createDashboardPreset,
				securityContext);
		dashboardPresetRepository.merge(dashboardPreset);
		return dashboardPreset;

	}

	public DashboardPreset createDashboardPresetNoMerge(
			DashboardPresetCreate createDashboardPreset, SecurityContext securityContext) {
		DashboardPreset dashboardPreset = new DashboardPreset(createDashboardPreset.getName(), securityContext);
		DashboardPresetUpdateNoMerge(createDashboardPreset, dashboardPreset);
		return dashboardPreset;
	}

	public PaginationResponse<DashboardPreset> getAllDashboardPreset(
			DashboardPresetFiltering dashboardPresetFiltering,
			SecurityContext securityContext) {
		List<DashboardPreset> list = listAllDashboardPreset(dashboardPresetFiltering,
				securityContext);
		long count = dashboardPresetRepository.countAllDashboardPreset(
				dashboardPresetFiltering, securityContext);
		return new PaginationResponse<>(list, dashboardPresetFiltering, count);
	}

	public void validate(DashboardPresetCreate createDashboardPreset,
			SecurityContext securityContext) {
		presetService.validate(createDashboardPreset, securityContext);
		String presetId=createDashboardPreset.getGridLayoutId();
		GridLayout gridPreset=presetId!=null?getByIdOrNull(presetId,GridLayout.class,null,securityContext):null;
		if(gridPreset==null){
			throw new BadRequestException("No GridLayout with id "+presetId);
		}
		createDashboardPreset.setGridLayout(gridPreset);
	}

	public void validate(DashboardPresetFiltering dashboardPresetFiltering,
			SecurityContext securityContext) {
		presetService.validate(dashboardPresetFiltering, securityContext);
		Set<String> presetIds=dashboardPresetFiltering.getGridLayoutIds();
		Map<String,GridLayout> presetMap=presetIds.isEmpty()?new HashMap<>():dashboardPresetRepository.listByIds(GridLayout.class,presetIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		presetIds.removeAll(presetMap.keySet());
		if(!presetIds.isEmpty()){
			throw new BadRequestException("No GridLayout with ids "+presetIds);
		}
		dashboardPresetFiltering.setGridLayouts(new ArrayList<>(presetMap.values()));
	}

}
