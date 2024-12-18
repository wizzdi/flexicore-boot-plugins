package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Basic;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
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
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@Extension
@Component
public class CellContentService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(CellContentService.class);


	@Autowired
	private CellContentRepository cellContentRepository;

	@Autowired
	private BasicService  baseclassNewService;

	public CellContent updateCellContent(CellContentUpdate cellContentUpdate, SecurityContext securityContext) {
		if (CellContentUpdateNoMerge(cellContentUpdate,
				cellContentUpdate.getCellContent())) {
			cellContentRepository.merge(cellContentUpdate.getCellContent());
		}
		return cellContentUpdate.getCellContent();
	}

	public boolean CellContentUpdateNoMerge(CellContentCreate cellContentCreate, CellContent cellContent) {
		boolean update = baseclassNewService.updateBasicNoMerge(cellContentCreate, cellContent);
		Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(cellContentCreate.any(), cellContent.any());
		if (map != null) {
			cellContent.setJsonNode(map);
			update = true;
		}
		return update;
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
		CellContent cellContent = new CellContent();
		cellContent.setId(UUID.randomUUID().toString());
		CellContentUpdateNoMerge(createCellContent, cellContent);
		BaseclassService.createSecurityObjectNoMerge(cellContent,securityContext);
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
		baseclassNewService.validate(cellContentFiltering, securityContext);

	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return cellContentRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return cellContentRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return cellContentRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return cellContentRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return cellContentRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return cellContentRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return cellContentRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		cellContentRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		cellContentRepository.massMerge(toMerge);
	}
}
