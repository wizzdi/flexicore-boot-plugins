package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.dashboard.data.GraphTemplateRepository;
import com.flexicore.ui.dashboard.model.CellContent;
import com.flexicore.ui.dashboard.model.GraphTemplate;
import com.flexicore.ui.dashboard.request.GraphTemplateCreate;
import com.flexicore.ui.dashboard.request.GraphTemplateFiltering;
import com.flexicore.ui.dashboard.request.GraphTemplateUpdate;
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
public class GraphTemplateService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(GraphTemplateService.class);

	@PluginInfo(version = 1)
	@Autowired
	private GraphTemplateRepository graphTemplateRepository;

	@Autowired
	private BaseclassNewService baseclassNewService;

	public GraphTemplate updateGraphTemplate(GraphTemplateUpdate graphTemplateUpdate, SecurityContext securityContext) {
		if (GraphTemplateUpdateNoMerge(graphTemplateUpdate,
				graphTemplateUpdate.getGraphTemplate())) {
			graphTemplateRepository.merge(graphTemplateUpdate.getGraphTemplate());
		}
		return graphTemplateUpdate.getGraphTemplate();
	}

	public boolean GraphTemplateUpdateNoMerge(GraphTemplateCreate graphTemplateCreate, GraphTemplate graphTemplate) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(graphTemplateCreate, graphTemplate);


		if(graphTemplateCreate.getFileResource()!=null&&(graphTemplate.getFileResource()==null||!graphTemplateCreate.getFileResource().getId().equals(graphTemplate.getFileResource().getId()))){
			graphTemplate.setFileResource(graphTemplateCreate.getFileResource());
			update=true;
		}



		return update;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return graphTemplateRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<GraphTemplate> listAllGraphTemplate(
			GraphTemplateFiltering graphTemplateFiltering,
			SecurityContext securityContext) {
		return graphTemplateRepository.listAllGraphTemplate(graphTemplateFiltering,
				securityContext);
	}

	public GraphTemplate createGraphTemplate(GraphTemplateCreate createGraphTemplate,
			SecurityContext securityContext) {
		GraphTemplate graphTemplate = createGraphTemplateNoMerge(createGraphTemplate,
				securityContext);
		graphTemplateRepository.merge(graphTemplate);
		return graphTemplate;

	}

	public GraphTemplate createGraphTemplateNoMerge(
			GraphTemplateCreate createGraphTemplate, SecurityContext securityContext) {
		GraphTemplate graphTemplate = new GraphTemplate(createGraphTemplate.getName(), securityContext);
		GraphTemplateUpdateNoMerge(createGraphTemplate, graphTemplate);
		return graphTemplate;
	}

	public PaginationResponse<GraphTemplate> getAllGraphTemplate(
			GraphTemplateFiltering graphTemplateFiltering,
			SecurityContext securityContext) {
		List<GraphTemplate> list = listAllGraphTemplate(graphTemplateFiltering,
				securityContext);
		long count = graphTemplateRepository.countAllGraphTemplate(
				graphTemplateFiltering, securityContext);
		return new PaginationResponse<>(list, graphTemplateFiltering, count);
	}

	public void validate(GraphTemplateCreate createGraphTemplate,
			SecurityContext securityContext) {
		baseclassNewService.validate(createGraphTemplate, securityContext);
		String fileResourceId=createGraphTemplate.getFileResourceId();
		FileResource cellContent=fileResourceId!=null?getByIdOrNull(fileResourceId, FileResource.class,null,securityContext):null;
		if(cellContent==null){
			throw new BadRequestException("No FileResource with id "+fileResourceId);
		}
		createGraphTemplate.setFileResource(cellContent);

	}

	public void validate(GraphTemplateFiltering graphTemplateFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(graphTemplateFiltering, securityContext);


	}

}
