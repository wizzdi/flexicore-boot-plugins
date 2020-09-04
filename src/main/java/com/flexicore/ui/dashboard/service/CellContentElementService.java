package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.dashboard.data.CellContentElementRepository;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.request.CellContentElementCreate;
import com.flexicore.ui.dashboard.request.CellContentElementFiltering;
import com.flexicore.ui.dashboard.request.CellContentElementUpdate;
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
public class CellContentElementService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(CellContentElementService.class);

	@PluginInfo(version = 1)
	@Autowired
	private CellContentElementRepository cellContentElementRepository;

	@Autowired
	@PluginInfo(version = 1)
	private BaseclassNewService baseclassNewService;

	public CellContentElement updateCellContentElement(CellContentElementUpdate cellContentElementUpdate, SecurityContext securityContext) {
		if (CellContentElementUpdateNoMerge(cellContentElementUpdate,
				cellContentElementUpdate.getCellContentElement())) {
			cellContentElementRepository.merge(cellContentElementUpdate.getCellContentElement());
		}
		return cellContentElementUpdate.getCellContentElement();
	}

	public boolean CellContentElementUpdateNoMerge(CellContentElementCreate cellContentElementCreate, CellContentElement cellContentElement) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(cellContentElementCreate, cellContentElement);


		if(cellContentElementCreate.getCellContent()!=null&&(cellContentElement.getCellContent()==null||!cellContentElementCreate.getCellContent().getId().equals(cellContentElement.getCellContent().getId()))){
			cellContentElement.setCellContent(cellContentElementCreate.getCellContent());
			update=true;
		}



		return update;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return cellContentElementRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<CellContentElement> listAllCellContentElement(
			CellContentElementFiltering cellContentElementFiltering,
			SecurityContext securityContext) {
		return cellContentElementRepository.listAllCellContentElement(cellContentElementFiltering,
				securityContext);
	}

	public CellContentElement createCellContentElement(CellContentElementCreate createCellContentElement,
			SecurityContext securityContext) {
		CellContentElement cellContentElement = createCellContentElementNoMerge(createCellContentElement,
				securityContext);
		cellContentElementRepository.merge(cellContentElement);
		return cellContentElement;

	}

	public CellContentElement createCellContentElementNoMerge(
			CellContentElementCreate createCellContentElement, SecurityContext securityContext) {
		CellContentElement cellContentElement = new CellContentElement(createCellContentElement.getName(), securityContext);
		CellContentElementUpdateNoMerge(createCellContentElement, cellContentElement);
		return cellContentElement;
	}

	public PaginationResponse<CellContentElement> getAllCellContentElement(
			CellContentElementFiltering cellContentElementFiltering,
			SecurityContext securityContext) {
		List<CellContentElement> list = listAllCellContentElement(cellContentElementFiltering,
				securityContext);
		long count = cellContentElementRepository.countAllCellContentElement(
				cellContentElementFiltering, securityContext);
		return new PaginationResponse<>(list, cellContentElementFiltering, count);
	}

	public void validate(CellContentElementCreate createCellContentElement,
			SecurityContext securityContext) {
		baseclassNewService.validate(createCellContentElement, securityContext);
		String cellContentId=createCellContentElement.getCellContentId();
		CellContent cellContent=cellContentId!=null?getByIdOrNull(cellContentId, CellContent.class,null,securityContext):null;
		if(cellContent==null){
			throw new BadRequestException("No CellContent with id "+cellContentId);
		}
		createCellContentElement.setCellContent(cellContent);

	}

	public void validate(CellContentElementFiltering cellContentElementFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(cellContentElementFiltering, securityContext);

		Set<String> cellContentIds=cellContentElementFiltering.getCellContentIds();
		Map<String,CellContent> cellContentMap=cellContentIds.isEmpty()?new HashMap<>():cellContentElementRepository.listByIds(CellContent.class,cellContentIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellContentIds.removeAll(cellContentMap.keySet());
		if(!cellContentIds.isEmpty()){
			throw new BadRequestException("No CellContent with ids "+cellContentIds);
		}
		cellContentElementFiltering.setCellContents(new ArrayList<>(cellContentMap.values()));

	}

}
