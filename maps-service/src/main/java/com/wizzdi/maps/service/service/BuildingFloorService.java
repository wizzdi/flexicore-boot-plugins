package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.BuildingFloor;
import com.wizzdi.maps.service.data.BuildingFloorRepository;
import com.wizzdi.maps.service.request.BuildingFloorCreate;
import com.wizzdi.maps.service.request.BuildingFloorFilter;
import com.wizzdi.maps.service.request.BuildingFloorUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.metamodel.SingularAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Extension
public class BuildingFloorService implements Plugin {

  @Autowired private BuildingFloorRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param buildingFloorCreate Object Used to Create BuildingFloor
   * @param securityContext
   * @return created BuildingFloor
   */
  public BuildingFloor createBuildingFloor(
          BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext) {
    BuildingFloor buildingFloor = createBuildingFloorNoMerge(buildingFloorCreate, securityContext);
    this.repository.merge(buildingFloor);
    return buildingFloor;
  }

  /**
   * @param buildingCreate Object Used to Create BuildingFloor
   * @param securityContext
   * @return created BuildingFloor unmerged
   */
  public BuildingFloor createBuildingFloorNoMerge(
      BuildingFloorCreate buildingCreate, SecurityContext securityContext) {
    BuildingFloor building = new BuildingFloor();
    building.setId(UUID.randomUUID().toString());
    updateBuildingFloorNoMerge(building, buildingCreate);

    BaseclassService.createSecurityObjectNoMerge(building, securityContext);

    return building;
  }

  /**
   * @param buildingFloorCreate Object Used to Create BuildingFloor
   * @param buildingFloor
   * @return if building was updated
   */
  public boolean updateBuildingFloorNoMerge(BuildingFloor buildingFloor, BuildingFloorCreate buildingFloorCreate) {
    boolean update = basicService.updateBasicNoMerge(buildingFloorCreate, buildingFloor);

    if (buildingFloorCreate.getBuilding() != null && (buildingFloor.getBuilding() == null || !buildingFloorCreate.getBuilding().getId().equals(buildingFloor.getBuilding().getId()))) {
      buildingFloor.setBuilding(buildingFloorCreate.getBuilding());
      update = true;
    }
    if (buildingFloorCreate.getDrawing() != null && (buildingFloor.getDrawing() == null || !buildingFloorCreate.getDrawing().getId().equals(buildingFloor.getDrawing().getId()))) {
      buildingFloor.setDrawing(buildingFloorCreate.getDrawing());
      update = true;
    }

    if (buildingFloorCreate.getExternalId() != null && !buildingFloorCreate.getExternalId().equals(buildingFloor.getExternalId())){
      buildingFloor.setExternalId(buildingFloorCreate.getExternalId());
      update = true;
    }
    return update;
  }
  /**
   * @param buildingFloorUpdate
   * @param securityContext
   * @return building
   */
  public BuildingFloor updateBuildingFloor(
          BuildingFloorUpdate buildingFloorUpdate, SecurityContext securityContext) {
    BuildingFloor buildingFloor = buildingFloorUpdate.getBuildingFloor();
    if (updateBuildingFloorNoMerge(buildingFloor, buildingFloorUpdate)) {
      this.repository.merge(buildingFloor);
    }
    return buildingFloor;
  }

  /**
   * @param buildingFilter Object Used to List BuildingFloor
   * @param securityContext
   * @return PaginationResponse containing paging information for BuildingFloor
   */
  public PaginationResponse<BuildingFloor> getAllBuildingFloors(
          BuildingFloorFilter buildingFilter, SecurityContext securityContext) {
    List<BuildingFloor> list = listAllBuildingFloors(buildingFilter, securityContext);
    long count = this.repository.countAllBuildingFloors(buildingFilter, securityContext);
    return new PaginationResponse<>(list, buildingFilter.getPageSize(), count);
  }

  /**
   * @param buildingFloorFilter Object Used to List BuildingFloor
   * @param securityContext
   * @return List of BuildingFloor
   */
  public List<BuildingFloor> listAllBuildingFloors(
      BuildingFloorFilter buildingFloorFilter, SecurityContext securityContext) {
    return this.repository.listAllBuildingFloors(buildingFloorFilter, securityContext);
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContext securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  public void merge(Object base) {
    this.repository.merge(base);
  }

  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }

  public BuildingFloor getOrCreateByExternalId(Building building, String floorId, SecurityContext gatewaySecurityContext) {
    String externalId= "%s_%s".formatted(gatewaySecurityContext.getTenantToCreateIn().getId(), floorId);
    return listAllBuildingFloors(new BuildingFloorFilter().setExternalIds(Collections.singleton(externalId)),null).stream().findFirst().orElseGet(()->createBuildingFloor(new BuildingFloorCreate().setBuilding(building).setExternalId(externalId).setName(floorId),gatewaySecurityContext));
  }
}
