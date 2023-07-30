package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.ui.dashboard.data.GraphTemplateRepository;
import com.flexicore.ui.dashboard.model.GraphTemplate;
import com.flexicore.ui.dashboard.request.GraphTemplateCreate;
import com.flexicore.ui.dashboard.request.GraphTemplateFilter;
import com.flexicore.ui.dashboard.request.GraphTemplateUpdate;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;


@Extension
@Component
public class GraphTemplateService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(GraphTemplateService.class);

	
	@Autowired
	private GraphTemplateRepository graphTemplateRepository;

	@Autowired
	private BasicService  baseclassNewService;

	public GraphTemplate updateGraphTemplate(GraphTemplateUpdate graphTemplateUpdate, SecurityContextBase securityContext) {
		if (GraphTemplateUpdateNoMerge(graphTemplateUpdate,
				graphTemplateUpdate.getGraphTemplate())) {
			graphTemplateRepository.merge(graphTemplateUpdate.getGraphTemplate());
		}
		return graphTemplateUpdate.getGraphTemplate();
	}

	public boolean GraphTemplateUpdateNoMerge(GraphTemplateCreate graphTemplateCreate, GraphTemplate graphTemplate) {
		boolean update = baseclassNewService.updateBasicNoMerge(graphTemplateCreate, graphTemplate);


		if(graphTemplateCreate.getFileResource()!=null&&(graphTemplate.getFileResource()==null||!graphTemplateCreate.getFileResource().getId().equals(graphTemplate.getFileResource().getId()))){
			graphTemplate.setFileResource(graphTemplateCreate.getFileResource());
			update=true;
		}

		Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(graphTemplateCreate.any(), graphTemplate.any());
		if (map != null) {
			graphTemplate.setJsonNode(map);
			update = true;
		}



		return update;
	}

	public List<GraphTemplate> listAllGraphTemplate(
			GraphTemplateFilter graphTemplateFilter,
			SecurityContextBase securityContext) {
		return graphTemplateRepository.listAllGraphTemplate(graphTemplateFilter,
				securityContext);
	}

	public GraphTemplate createGraphTemplate(GraphTemplateCreate createGraphTemplate,
			SecurityContextBase securityContext) {
		GraphTemplate graphTemplate = createGraphTemplateNoMerge(createGraphTemplate,
				securityContext);
		graphTemplateRepository.merge(graphTemplate);
		return graphTemplate;

	}

	public GraphTemplate createGraphTemplateNoMerge(
			GraphTemplateCreate createGraphTemplate, SecurityContextBase securityContext) {
		GraphTemplate graphTemplate = new GraphTemplate();
		graphTemplate.setId(UUID.randomUUID().toString());
		GraphTemplateUpdateNoMerge(createGraphTemplate, graphTemplate);
		BaseclassService.createSecurityObjectNoMerge(graphTemplate,securityContext);
		return graphTemplate;
	}

	public PaginationResponse<GraphTemplate> getAllGraphTemplate(
			GraphTemplateFilter graphTemplateFilter,
			SecurityContextBase securityContext) {
		List<GraphTemplate> list = listAllGraphTemplate(graphTemplateFilter,
				securityContext);
		long count = graphTemplateRepository.countAllGraphTemplate(
				graphTemplateFilter, securityContext);
		return new PaginationResponse<>(list, graphTemplateFilter, count);
	}

	public void validate(GraphTemplateCreate createGraphTemplate,
			SecurityContextBase securityContext) {
		baseclassNewService.validate(createGraphTemplate, securityContext);
		String fileResourceId=createGraphTemplate.getFileResourceId();
		FileResource cellContent=fileResourceId!=null?getByIdOrNull(fileResourceId, FileResource.class, SecuredBasic_.security,securityContext):null;
		if(cellContent==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No FileResource with id "+fileResourceId);
		}
		createGraphTemplate.setFileResource(cellContent);

	}

	public void validate(GraphTemplateFilter graphTemplateFilter,
						 SecurityContextBase securityContext) {
		baseclassNewService.validate(graphTemplateFilter, securityContext);


	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return graphTemplateRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return graphTemplateRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return graphTemplateRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return graphTemplateRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return graphTemplateRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return graphTemplateRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return graphTemplateRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		graphTemplateRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		graphTemplateRepository.massMerge(toMerge);
	}
}
