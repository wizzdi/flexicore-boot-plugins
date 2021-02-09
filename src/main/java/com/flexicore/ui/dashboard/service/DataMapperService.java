package com.flexicore.ui.dashboard.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.flexicore.ui.dashboard.data.DataMapperRepository;
import com.flexicore.ui.dashboard.model.CellContentElement;
import com.flexicore.ui.dashboard.model.CellToLayout;
import com.flexicore.ui.dashboard.model.DataMapper;
import com.flexicore.ui.dashboard.request.DataMapperCreate;
import com.flexicore.ui.dashboard.request.DataMapperFiltering;
import com.flexicore.ui.dashboard.request.DataMapperUpdate;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
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
public class DataMapperService implements ServicePlugin {

	private static final Logger logger= LoggerFactory.getLogger(DataMapperService.class);

	@PluginInfo(version = 1)
	@Autowired
	private DataMapperRepository dataMapperRepository;

	@Autowired
	private BaseclassNewService baseclassNewService;

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
		boolean update = baseclassNewService.updateBaseclassNoMerge(dataMapperCreate, dataMapper);

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
		if (dataMapperCreate.any() != null && !dataMapperCreate.any().isEmpty()) {
			Map<String, Object> jsonNode = dataMapper.getJsonNode();
			if (jsonNode == null) {
				dataMapper.setJsonNode(dataMapperCreate.any());
				update = true;
			} else {
				for (Map.Entry<String, Object> entry : dataMapperCreate.any().entrySet()) {
					String key = entry.getKey();
					Object newVal = entry.getValue();
					Object val = jsonNode.get(key);
					if (newVal != null && !newVal.equals(val)) {
						jsonNode.put(key, newVal);
						update = true;
					}
				}
			}
		}

		return update;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
												 List<String> batchString, SecurityContext securityContext) {
		return dataMapperRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public List<DataMapper> listAllDataMapper(
			DataMapperFiltering dataMapperFiltering,
			SecurityContext securityContext) {
		return dataMapperRepository.listAllDataMapper(dataMapperFiltering,
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
		DataMapper dataMapper = new DataMapper(createDataMapper.getName(), securityContext);
		DataMapperUpdateNoMerge(createDataMapper, dataMapper);
		return dataMapper;
	}

	public PaginationResponse<DataMapper> getAllDataMapper(
			DataMapperFiltering dataMapperFiltering,
			SecurityContext securityContext) {
		List<DataMapper> list = listAllDataMapper(dataMapperFiltering,
				securityContext);
		long count = dataMapperRepository.countAllDataMapper(
				dataMapperFiltering, securityContext);
		return new PaginationResponse<>(list, dataMapperFiltering, count);
	}

	public void validate(DataMapperCreate createDataMapper,
			SecurityContext securityContext) {
		baseclassNewService.validate(createDataMapper, securityContext);
		String cellToLayoutId=createDataMapper.getCellToLayoutId();
		CellToLayout cellToLayout=cellToLayoutId!=null?getByIdOrNull(cellToLayoutId, CellToLayout.class,null,securityContext):null;
		if(cellToLayout==null&&cellToLayoutId!=null){
			throw new BadRequestException("No CellToLayout with id "+cellToLayoutId);
		}
		createDataMapper.setCellToLayout(cellToLayout);

		String cellContentElementId=createDataMapper.getCellContentElementId();
		CellContentElement cellContentElement=cellContentElementId!=null?getByIdOrNull(cellContentElementId, CellContentElement.class,null,securityContext):null;
		if(cellContentElement==null&&cellContentElementId!=null){
			throw new BadRequestException("No CellContentElement with id "+cellContentElementId);
		}
		createDataMapper.setCellContentElement(cellContentElement);


		String dynamicExecutionId=createDataMapper.getDynamicExecutionId();
		DynamicExecution dynamicExecution=dynamicExecutionId!=null?dynamicExecutionService.getByIdOrNull(dynamicExecutionId, DynamicExecution.class,securityContext):null;
		if(dynamicExecution==null&&dynamicExecutionId!=null){
			throw new BadRequestException("No DynamicExecution with id "+dynamicExecutionId);
		}
		createDataMapper.setDynamicExecution(dynamicExecution);



	}

	public void validate(DataMapperFiltering dataMapperFiltering,
			SecurityContext securityContext) {
		baseclassNewService.validateFilter(dataMapperFiltering, securityContext);

		Set<String> cellToLayoutIds=dataMapperFiltering.getCellToLayoutIds();
		Map<String, CellToLayout> cellToLayoutMap=cellToLayoutIds.isEmpty()?new HashMap<>():dataMapperRepository.listByIds(CellToLayout.class,cellToLayoutIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellToLayoutIds.removeAll(cellToLayoutMap.keySet());
		if(!cellToLayoutIds.isEmpty()){
			throw new BadRequestException("No CellToLayout with ids "+cellToLayoutIds);
		}
		dataMapperFiltering.setCellToLayouts(new ArrayList<>(cellToLayoutMap.values()));


		Set<String> cellContentElementIds=dataMapperFiltering.getCellContentElementIds();
		Map<String, CellContentElement> cellContentElementMap=cellContentElementIds.isEmpty()?new HashMap<>():dataMapperRepository.listByIds(CellContentElement.class,cellContentElementIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		cellContentElementIds.removeAll(cellContentElementMap.keySet());
		if(!cellContentElementIds.isEmpty()){
			throw new BadRequestException("No CellContentElement with ids "+cellContentElementIds);
		}
		dataMapperFiltering.setCellContentElements(new ArrayList<>(cellContentElementMap.values()));


		Set<String> dynamicExecutionIds=dataMapperFiltering.getDynamicExecutionIds();
		Map<String, DynamicExecution> dynamicExecutionMap=dynamicExecutionIds.isEmpty()?new HashMap<>():dynamicExecutionService.listByIds(dynamicExecutionIds,DynamicExecution.class,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f,(a, b)->a));
		dynamicExecutionIds.removeAll(dynamicExecutionMap.keySet());
		if(!dynamicExecutionIds.isEmpty()){
			throw new BadRequestException("No DynamicExecution with ids "+dynamicExecutionIds);
		}
		dataMapperFiltering.setDynamicExecutions(new ArrayList<>(dynamicExecutionMap.values()));




	}

}
