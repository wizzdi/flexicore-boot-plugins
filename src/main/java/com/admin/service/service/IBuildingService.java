package com.admin.service.service;

import com.admin.model.Building;
import com.admin.service.request.BuildingCreate;
import com.admin.service.request.BuildingFilter;
import com.admin.service.request.BuildingUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IBuildingService {

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Building
   */
  Building createBuilding(BuildingCreate buildingCreate, SecurityContextBase securityContext);

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param securityContext
   * @return created Building unmerged
   */
  Building createBuildingNoMerge(
      BuildingCreate buildingCreate, SecurityContextBase securityContext);

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param building
   * @return if building was updated
   */
  boolean updateBuildingNoMerge(Building building, BuildingCreate buildingCreate);

  /**
   * @param buildingUpdate
   * @param securityContext
   * @return building
   */
  Building updateBuilding(BuildingUpdate buildingUpdate, SecurityContextBase securityContext);

  /**
   * @param buildingFilter Object Used to List Entity1
   * @param securityContext
   * @return PaginationResponse containing paging information for Building
   */
  PaginationResponse<Building> getAllBuildings(
      BuildingFilter buildingFilter, SecurityContextBase securityContext);

  /**
   * @param buildingFilter Object Used to List Entity1
   * @param securityContext
   * @return List of Building
   */
  List<Building> listAllBuildings(
      BuildingFilter buildingFilter, SecurityContextBase securityContext);

  /**
   * @param buildingFilter Object Used to List Entity1
   * @param securityContext
   * @throws ResponseStatusException if buildingFilter is not valid
   */
  void validate(BuildingFilter buildingFilter, SecurityContextBase securityContext);

  /**
   * @param buildingCreate Object Used to Create Entity1
   * @param securityContext
   * @throws ResponseStatusException if buildingCreate is not valid
   */
  void validate(BuildingCreate buildingCreate, SecurityContextBase securityContext);

  <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext);

  <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute);

  <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested);

  <T> T findByIdOrNull(Class<T> type, String id);

  void merge(java.lang.Object base);

  void massMerge(List<?> toMerge);
}
