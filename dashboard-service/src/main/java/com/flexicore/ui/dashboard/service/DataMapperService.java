package com.flexicore.ui.dashboard.service;


import com.flexicore.model.Basic;

import com.wizzdi.dynamic.properties.converter.DynamicPropertiesUtils;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.ui.dashboard.data.DataMapperRepository;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.model.CellToLayout;
import com.flexicore.ui.dashboard.model.DataMapper;
import com.flexicore.ui.dashboard.request.DataMapperCreate;
import com.flexicore.ui.dashboard.request.DataMapperFilter;
import com.flexicore.ui.dashboard.request.DataMapperUpdate;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
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
import java.util.stream.Collectors;


@Extension
@Component
public class DataMapperService implements Plugin {

	private static final Logger logger= LoggerFactory.getLogger(DataMapperService.class);

	
	@Autowired
	private DataMapperRepository dataMapperRepository;

	@Autowired
	private BasicService  baseclassNewService;

	@Autowired
	private DynamicExecutionService dynamicExecutionService;

	public DataMapper updateDataMapper(DataMapperUpdate dataMapperUpdate, SecurityContext securityContext) {
		if (DataMapperUpdateNoMerge(dataMapperUpdate,
				dataMapperUpdate.getDataMapper())) {
			dataMapperRepository.merge(dataMapperUpdate.getDataMapper());
		}
		return dataMapperUpdate.getDataMapper();
	}

	public boolean DataMapperUpdateNoMerge(DataMapperCreate dataMapperCreate, DataMapper dataMapper) {
		boolean update = baseclassNewService.updateBasicNoMerge(dataMapperCreate, dataMapper);

		if(dataMapperCreate.getCellContentElement()!=null&&(dataMapper.getCellContentElement()==null||!dataMapperCreate.getCellContentElement().getId().equals(dataMapper.getCellContentElement().getId()))){
			dataMapper.setCellContentElement(dataMapperCreate.getCellContentElement());
			update=true;
		}

		if(dataMapperCreate.getCellToLayout()!=null&&(dataMapper.getCellToLayout()==null||!dataMapperCreate.getCellToLayout().getId().equals(dataMapper.getCellToLayout().getId()))){
			dataMapper.setCellToLayout(dataMapperCreate.getCellToLayout());
			update=true;
		}

		if(dataMapperCreate.getDynamicExecution()!=null&&(dataMapper.getDynamicExecution()==null||!dataMapperCreate.getDynamicExecution().getId().equals(dataMapper.getDynamicExecution().getId()))){
			dataMapper.setDynamicExecution(dataMapperCreate.getDynamicExecution());
			update=true;
		}

		if(dataMapperCreate.getFieldPath()!=null&&!dataMapperCreate.getFieldPath().equals(dataMapper.getFieldPath())){
			dataMapper.setFieldPath(dataMapperCreate.getFieldPath());
			update=true;
		}
		if(dataMapperCreate.getStaticData()!=null&&!dataMapperCreate.getStaticData().equals(dataMapper.getStaticData())){
			dataMapper.setStaticData(dataMapperCreate.getStaticData());
			update=true;
		}

		Map<String, Object> map = DynamicPropertiesUtils.updateDynamic(dataMapperCreate.any(), dataMapper.any());
		if (map != null) {
			dataMapper.setJsonNode(map);
			update = true;
		}
		return update;
	}

	public List<DataMapper> listAllDataMapper(
			DataMapperFilter dataMapperFilter,
			SecurityContext securityContext) {
		return dataMapperRepository.listAllDataMapper(dataMapperFilter,
				securityContext);
	}

	public DataMapper createDataMapper(DataMapperCreate createDataMapper,
			SecurityContext securityContext) {
		DataMapper dataMapper = createDataMapperNoMerge(createDataMapper,
				securityContext);
		dataMapperRepository.merge(dataMapper);
		return dataMapper;

	}

	public DataMapper createDataMapperNoMerge(
			DataMapperCreate createDataMapper, SecurityContext securityContext) {
		DataMapper dataMapper = new DataMapper();
		dataMapper.setId(UUID.randomUUID().toString());
		DataMapperUpdateNoMerge(createDataMapper, dataMapper);
		BaseclassService.createSecurityObjectNoMerge(dataMapper,securityContext);
		return dataMapper;
	}

	public PaginationResponse<DataMapper> getAllDataMapper(
			DataMapperFilter dataMapperFilter,
			SecurityContext securityContext) {
		List<DataMapper> list = listAllDataMapper(dataMapperFilter,
				securityContext);
		long count = dataMapperRepository.countAllDataMapper(
				dataMapperFilter, securityContext);
		return new PaginationResponse<>(list, dataMapperFilter, count);
	}

	public void validate(DataMapperCreate createDataMapper,
			SecurityContext securityContext) {
		baseclassNewService.validate(createDataMapper, securityContext);
		String cellToLayoutId=createDataMapper.getCellToLayoutId();
		CellToLayout cellToLayout=cellToLayoutId!=null?getByIdOrNull(cellToLayoutId, CellToLayout.class,securityContext):null;
		if(cellToLayout==null&&cellToLayoutId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellToLayout with id "+cellToLayoutId);
		}
		createDataMapper.setCellToLayout(cellToLayout);

		String cellContentElementId=createDataMapper.getCellContentElementId();
		CellContentElement cellContentElement=cellContentElementId!=null?getByIdOrNull(cellContentElementId, CellContentElement.class,securityContext):null;
		if(cellContentElement==null&&cellContentElementId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellContentElement with id "+cellContentElementId);
		}
		createDataMapper.setCellContentElement(cellContentElement);


		String dynamicExecutionId=createDataMapper.getDynamicExecutionId();
		DynamicExecution dynamicExecution=dynamicExecutionId!=null?dynamicExecutionService.getByIdOrNull(dynamicExecutionId, DynamicExecution.class, securityContext):null;
		if(dynamicExecution==null&&dynamicExecutionId!=null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No DynamicExecution with id "+dynamicExecutionId);
		}
		createDataMapper.setDynamicExecution(dynamicExecution);



	}

	public void validate(DataMapperFilter dataMapperFilter,
						 SecurityContext securityContext) {
		baseclassNewService.validate(dataMapperFilter, securityContext);

		Set<String> cellToLayoutIds= dataMapperFilter.getCellToLayoutIds();
		Map<String, CellToLayout> cellToLayoutMap=cellToLayoutIds.isEmpty()?new HashMap<>():dataMapperRepository.listByIds(CellToLayout.class,cellToLayoutIds, securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellToLayoutIds.removeAll(cellToLayoutMap.keySet());
		if(!cellToLayoutIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellToLayout with ids "+cellToLayoutIds);
		}
		dataMapperFilter.setCellToLayouts(new ArrayList<>(cellToLayoutMap.values()));


		Set<String> cellContentElementIds= dataMapperFilter.getCellContentElementIds();
		Map<String, CellContentElement> cellContentElementMap=cellContentElementIds.isEmpty()?new HashMap<>():dataMapperRepository.listByIds(CellContentElement.class,cellContentElementIds, securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellContentElementIds.removeAll(cellContentElementMap.keySet());
		if(!cellContentElementIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No CellContentElement with ids "+cellContentElementIds);
		}
		dataMapperFilter.setCellContentElements(new ArrayList<>(cellContentElementMap.values()));


		Set<String> dynamicExecutionIds= dataMapperFilter.getDynamicExecutionIds();
		Map<String, DynamicExecution> dynamicExecutionMap=dynamicExecutionIds.isEmpty()?new HashMap<>():dynamicExecutionService.listByIds(DynamicExecution.class,dynamicExecutionIds, securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		dynamicExecutionIds.removeAll(dynamicExecutionMap.keySet());
		if(!dynamicExecutionIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No DynamicExecution with ids "+dynamicExecutionIds);
		}
		dataMapperFilter.setDynamicExecutions(new ArrayList<>(dynamicExecutionMap.values()));




	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return dataMapperRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return dataMapperRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return dataMapperRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return dataMapperRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return dataMapperRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return dataMapperRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return dataMapperRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		dataMapperRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		dataMapperRepository.massMerge(toMerge);
	}
}
