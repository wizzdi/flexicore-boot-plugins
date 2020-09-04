package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.dashboard.data.CellContentRepository;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.request.CellContentCreate;
import com.flexicore.ui.dashboard.request.CellContentFiltering;
import com.flexicore.ui.dashboard.request.CellContentUpdate;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@PluginInfo(version = 1)
@Extension
@Component
public class CellContentService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(CellContentService.class);

	@PluginInfo(version = 1)
	@Autowired
	private CellContentRepository cellContentRepository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public CellContent updateCellContent(CellContentUpdate cellContentUpdate, SecurityContext securityContext) {
		if (CellContentUpdateNoMerge(cellContentUpdate,
				cellContentUpdate.getCellContent())) {
			cellContentRepository.merge(cellContentUpdate.getCellContent());
		}
		return cellContentUpdate.getCellContent();
	}

	public boolean CellContentUpdateNoMerge(CellContentCreate cellContentCreate, CellContent cellContent) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(cellContentCreate, cellContent);

		return update;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return cellContentRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<CellContent> listAllCellContent(
			CellContentFiltering cellContentFiltering,
			SecurityContext securityContext) {
		return cellContentRepository.listAllCellContent(cellContentFiltering,
				securityContext);
	}

	public CellContent createCellContent(CellContentCreate createCellContent,
			SecurityContext securityContext) {
		CellContent cellContent = createCellContentNoMerge(createCellContent,
				securityContext);
		cellContentRepository.merge(cellContent);
		return cellContent;

	}

	public CellContent createCellContentNoMerge(
			CellContentCreate createCellContent, SecurityContext securityContext) {
		CellContent cellContent = new CellContent(createCellContent.getName(), securityContext);
		CellContentUpdateNoMerge(createCellContent, cellContent);
		return cellContent;
	}

	public PaginationResponse<CellContent> getAllCellContent(
			CellContentFiltering cellContentFiltering,
			SecurityContext securityContext) {
		List<CellContent> list = listAllCellContent(cellContentFiltering,
				securityContext);
		long count = cellContentRepository.countAllCellContent(
				cellContentFiltering, securityContext);
		return new PaginationResponse<>(list, cellContentFiltering, count);
	}

	public void validate(CellContentCreate createCellContent,
			SecurityContext securityContext) {
		baseclassNewService.validate(createCellContent, securityContext);

	}

	public void validate(CellContentFiltering cellContentFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(cellContentFiltering, securityContext);

	}

}
