package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.ui.dashboard.data.CellContentElementRepository;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.request.CellContentElementCreate;
import com.flexicore.ui.dashboard.request.CellContentElementFilter;
import com.flexicore.ui.dashboard.request.CellContentElementUpdate;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component
public class CellContentElementService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(CellContentElementService.class);


	@Autowired
	private CellContentElementRepository cellContentElementRepository;

	@Autowired
	private BasicService baseclassNewService;

	public CellContentElement updateCellContentElement(CellContentElementUpdate cellContentElementUpdate, SecurityContextBase securityContext) {
		if (CellContentElementUpdateNoMerge(cellContentElementUpdate,
				cellContentElementUpdate.getCellContentElement())) {
			cellContentElementRepository.merge(cellContentElementUpdate.getCellContentElement());
		}
		return cellContentElementUpdate.getCellContentElement();
	}

	public boolean CellContentElementUpdateNoMerge(CellContentElementCreate cellContentElementCreate, CellContentElement cellContentElement) {
		boolean update = baseclassNewService.updateBasicNoMerge(cellContentElementCreate, cellContentElement);


		if(cellContentElementCreate.getCellContent()!=null&&(cellContentElement.getCellContent()==null||!cellContentElementCreate.getCellContent().getId().equals(cellContentElement.getCellContent().getId()))){
			cellContentElement.setCellContent(cellContentElementCreate.getCellContent());
			update=true;
		}

		Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(cellContentElementCreate.any(), cellContentElement.any());
		if (map != null) {
			cellContentElement.setJsonNode(map);
			update = true;
		}
	


		return update;
	}
	

	public List<CellContentElement> listAllCellContentElement(
			CellContentElementFilter cellContentElementFilter,
			SecurityContextBase securityContext) {
		return cellContentElementRepository.listAllCellContentElement(cellContentElementFilter,
				securityContext);
	}

	public CellContentElement createCellContentElement(CellContentElementCreate createCellContentElement,
			SecurityContextBase securityContext) {
		CellContentElement cellContentElement = createCellContentElementNoMerge(createCellContentElement,
				securityContext);
		cellContentElementRepository.merge(cellContentElement);
		return cellContentElement;

	}

	public CellContentElement createCellContentElementNoMerge(
			CellContentElementCreate createCellContentElement, SecurityContextBase securityContext) {
		CellContentElement cellContentElement = new CellContentElement();
		cellContentElement.setId(UUID.randomUUID().toString());
		CellContentElementUpdateNoMerge(createCellContentElement, cellContentElement);
		BaseclassService.createSecurityObjectNoMerge(cellContentElement,securityContext);
		return cellContentElement;
	}

	public PaginationResponse<CellContentElement> getAllCellContentElement(
			CellContentElementFilter cellContentElementFilter,
			SecurityContextBase securityContext) {
		List<CellContentElement> list = listAllCellContentElement(cellContentElementFilter,
				securityContext);
		long count = cellContentElementRepository.countAllCellContentElement(
				cellContentElementFilter, securityContext);
		return new PaginationResponse<>(list, cellContentElementFilter, count);
	}

	public void validate(CellContentElementCreate createCellContentElement,
			SecurityContextBase securityContext) {
		baseclassNewService.validate(createCellContentElement, securityContext);
		String cellContentId=createCellContentElement.getCellContentId();
		CellContent cellContent=cellContentId!=null?getByIdOrNull(cellContentId, CellContent.class,SecuredBasic_.security,securityContext):null;
		if(cellContent==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellContent with id "+cellContentId);
		}
		createCellContentElement.setCellContent(cellContent);

	}

	public void validate(CellContentElementFilter cellContentElementFilter,
						 SecurityContextBase securityContext) {
		baseclassNewService.validate(cellContentElementFilter, securityContext);

		Set<String> cellContentIds= cellContentElementFilter.getCellContentIds();
		Map<String,CellContent> cellContentMap=cellContentIds.isEmpty()?new HashMap<>():cellContentElementRepository.listByIds(CellContent.class,cellContentIds, SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellContentIds.removeAll(cellContentMap.keySet());
		if(!cellContentIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellContent with ids "+cellContentIds);
		}
		cellContentElementFilter.setCellContents(new ArrayList<>(cellContentMap.values()));

	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return cellContentElementRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return cellContentElementRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return cellContentElementRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return cellContentElementRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return cellContentElementRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return cellContentElementRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return cellContentElementRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		cellContentElementRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		cellContentElementRepository.massMerge(toMerge);
	}
}
