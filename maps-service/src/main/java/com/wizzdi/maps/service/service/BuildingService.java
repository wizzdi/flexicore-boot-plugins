package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.service.data.BuildingRepository;
import com.wizzdi.maps.service.request.BuildingCreate;
import com.wizzdi.maps.service.request.BuildingFilter;
import com.wizzdi.maps.service.request.BuildingUpdate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class BuildingService implements Plugin {

  @Autowired private BuildingRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param buildingCreate Object Used to Create Building
   * @param securityContext
   * @return created Building
   */
  public Building createBuilding(
      BuildingCreate buildingCreate, SecurityContextBase securityContext) {
    Building building = createBuildingNoMerge(buildingCreate, securityContext);
    this.repository.merge(building);
    return building;
  }

  /**
   * @param buildingCreate Object Used to Create Building
   * @param securityContext
   * @return created Building unmerged
   */
  public Building createBuildingNoMerge(
      BuildingCreate buildingCreate, SecurityContextBase securityContext) {
    Building building = new Building();
    building.setId(UUID.randomUUID().toString());
    updateBuildingNoMerge(building, buildingCreate);

    BaseclassService.createSecurityObjectNoMerge(building, securityContext);

    return building;
  }

  /**
   * @param buildingCreate Object Used to Create Building
   * @param building
   * @return if building was updated
   */
  public boolean updateBuildingNoMerge(Building building, BuildingCreate buildingCreate) {
    boolean update = basicService.updateBasicNoMerge(buildingCreate, building);

    if (buildingCreate.getMappedPOI() != null
        && (building.getMappedPOI() == null
            || !buildingCreate.getMappedPOI().getId().equals(building.getMappedPOI().getId()))) {
      building.setMappedPOI(buildingCreate.getMappedPOI());
      update = true;
    }

    if (buildingCreate.getExternalId() != null
        && (!buildingCreate.getExternalId().equals(building.getExternalId()))) {
      building.setExternalId(buildingCreate.getExternalId());
      update = true;
    }

    return update;
  }
  /**
   * @param buildingUpdate
   * @param securityContext
   * @return building
   */
  public Building updateBuilding(
      BuildingUpdate buildingUpdate, SecurityContextBase securityContext) {
    Building building = buildingUpdate.getBuilding();
    if (updateBuildingNoMerge(building, buildingUpdate)) {
      this.repository.merge(building);
    }
    return building;
  }

  /**
   * @param buildingFilter Object Used to List Building
   * @param securityContext
   * @return PaginationResponse containing paging information for Building
   */
  public PaginationResponse<Building> getAllBuildings(
      BuildingFilter buildingFilter, SecurityContextBase securityContext) {
    List<Building> list = listAllBuildings(buildingFilter, securityContext);
    long count = this.repository.countAllBuildings(buildingFilter, securityContext);
    return new PaginationResponse<>(list, buildingFilter.getPageSize(), count);
  }

  /**
   * @param buildingFilter Object Used to List Building
   * @param securityContext
   * @return List of Building
   */
  public List<Building> listAllBuildings(
      BuildingFilter buildingFilter, SecurityContextBase securityContext) {
    return this.repository.listAllBuildings(buildingFilter, securityContext);
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
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

  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
