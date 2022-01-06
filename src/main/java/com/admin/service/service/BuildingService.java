package com.admin.service.service;

import com.admin.model.Building;
import com.admin.service.data.BuildingRepository;
import com.admin.service.request.BuildingCreate;
import com.admin.service.request.BuildingFilter;
import com.admin.service.request.BuildingUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.MappedPOI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class BuildingService implements Plugin, IBuildingService {

  @Autowired private BuildingRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Building
   */
  @Override
  public Building createBuilding(
      BuildingCreate buildingCreate, SecurityContextBase securityContext) {
    Building building = createBuildingNoMerge(buildingCreate, securityContext);
    this.repository.merge(building);
    return building;
  }

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Building unmerged
   */
  @Override
  public Building createBuildingNoMerge(
      BuildingCreate buildingCreate, SecurityContextBase securityContext) {
    Building building = new Building();
    building.setId(UUID.randomUUID().toString());
    updateBuildingNoMerge(building, buildingCreate);

    BaseclassService.createSecurityObjectNoMerge(building, securityContext);

    return building;
  }

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param building
   * @return if building was updated
   */
  @Override
  public boolean updateBuildingNoMerge(Building building, BuildingCreate buildingCreate) {
    boolean update = basicService.updateBasicNoMerge(buildingCreate, building);

    if (buildingCreate.getExternalId() != null
        && (!buildingCreate.getExternalId().equals(building.getExternalId()))) {
      building.setExternalId(buildingCreate.getExternalId());
      update = true;
    }

    if (buildingCreate.getMappedPOI() != null
        && (building.getMappedPOI() == null
            || !buildingCreate.getMappedPOI().getId().equals(building.getMappedPOI().getId()))) {
      building.setMappedPOI(buildingCreate.getMappedPOI());
      update = true;
    }

    return update;
  }
  /**
   * @param buildingUpdate
   * @param securityContext
   * @return building
   */
  @Override
  public Building updateBuilding(
      BuildingUpdate buildingUpdate, SecurityContextBase securityContext) {
    Building building = buildingUpdate.getBuilding();
    if (updateBuildingNoMerge(building, buildingUpdate)) {
      this.repository.merge(building);
    }
    return building;
  }

  /**
   * @param buildingFilter Object Used to List Entity1
   * @param securityContext
   * @return PaginationResponse containing paging information for Building
   */
  @Override
  public PaginationResponse<Building> getAllBuildings(
      BuildingFilter buildingFilter, SecurityContextBase securityContext) {
    List<Building> list = listAllBuildings(buildingFilter, securityContext);
    long count = this.repository.countAllBuildings(buildingFilter, securityContext);
    return new PaginationResponse<>(list, buildingFilter, count);
  }

  /**
   * @param buildingFilter Object Used to List Entity1
   * @param securityContext
   * @return List of Building
   */
  @Override
  public List<Building> listAllBuildings(
      BuildingFilter buildingFilter, SecurityContextBase securityContext) {
    return this.repository.listAllBuildings(buildingFilter, securityContext);
  }

  /**
   * @param buildingFilter Object Used to List Entity1
   * @param securityContext
   * @throws ResponseStatusException if buildingFilter is not valid
   */
  @Override
  public void validate(BuildingFilter buildingFilter, SecurityContextBase securityContext) {
    basicService.validate(buildingFilter, securityContext);

    Set<String> mappedPOIIds =
        buildingFilter.getMappedPOIIds() == null
            ? new HashSet<>()
            : buildingFilter.getMappedPOIIds();
    Map<String, MappedPOI> mappedPOI =
        mappedPOIIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(MappedPOI.class, mappedPOIIds, SecuredBasic_.security, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    mappedPOIIds.removeAll(mappedPOI.keySet());
    if (!mappedPOIIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No MappedPOI with ids " + mappedPOIIds);
    }
    buildingFilter.setMappedPOI(new ArrayList<>(mappedPOI.values()));
  }

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param securityContext
   * @throws ResponseStatusException if buildingCreate is not valid
   */
  @Override
  public void validate(BuildingCreate buildingCreate, SecurityContextBase securityContext) {
    basicService.validate(buildingCreate, securityContext);

    String mappedPOIId = buildingCreate.getMappedPOIId();
    MappedPOI mappedPOI =
        mappedPOIId == null
            ? null
            : this.repository.getByIdOrNull(
                mappedPOIId, MappedPOI.class, SecuredBasic_.security, securityContext);
    if (mappedPOIId != null && mappedPOI == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No MappedPOI with id " + mappedPOIId);
    }
    buildingCreate.setMappedPOI(mappedPOI);
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  @Override
  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  @Override
  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  @Override
  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  @Override
  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
