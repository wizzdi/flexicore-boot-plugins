package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.service.data.StatusHistoryRepository;
import com.wizzdi.maps.service.request.StatusHistoryCreate;
import com.wizzdi.maps.service.request.StatusHistoryFilter;
import com.wizzdi.maps.service.request.StatusHistoryUpdate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.wizzdi.maps.service.response.StatusHistoryContainer;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class StatusHistoryService implements Plugin {

  @Autowired private StatusHistoryRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param statusHistoryCreate Object Used to Create StatusHistory
   * @param securityContext
   * @return created StatusHistory
   */
  public StatusHistory createStatusHistory(
      StatusHistoryCreate statusHistoryCreate, SecurityContextBase securityContext) {
    StatusHistory statusHistory = createStatusHistoryNoMerge(statusHistoryCreate, securityContext);
    this.repository.merge(statusHistory);
    return statusHistory;
  }

  /**
   * @param statusHistoryCreate Object Used to Create StatusHistory
   * @param securityContext
   * @return created StatusHistory unmerged
   */
  public StatusHistory createStatusHistoryNoMerge(
      StatusHistoryCreate statusHistoryCreate, SecurityContextBase securityContext) {
    StatusHistory statusHistory = new StatusHistory();
    statusHistory.setId(UUID.randomUUID().toString());
    updateStatusHistoryNoMerge(statusHistory, statusHistoryCreate);

    if(statusHistory.getMappedPOI()!=null){
      statusHistory.setSecurity(statusHistory.getMappedPOI().getSecurity());
    }
    else{
      throw new RuntimeException("cannot create status history without MappedPOI");
    }
    return statusHistory;
  }

  /**
   * @param statusHistoryCreate Object Used to Create StatusHistory
   * @param statusHistory
   * @return if statusHistory was updated
   */
  public boolean updateStatusHistoryNoMerge(
      StatusHistory statusHistory, StatusHistoryCreate statusHistoryCreate) {
    boolean update = basicService.updateBasicNoMerge(statusHistoryCreate, statusHistory);

    if (statusHistoryCreate.getMappedPOI() != null
        && (statusHistory.getMappedPOI() == null
            || !statusHistoryCreate
                .getMappedPOI()
                .getId()
                .equals(statusHistory.getMappedPOI().getId()))) {
      statusHistory.setMappedPOI(statusHistoryCreate.getMappedPOI());
      update = true;
    }

    if (statusHistoryCreate.getMapIcon() != null
        && (statusHistory.getMapIcon() == null
            || !statusHistoryCreate
                .getMapIcon()
                .getId()
                .equals(statusHistory.getMapIcon().getId()))) {
      statusHistory.setMapIcon(statusHistoryCreate.getMapIcon());
      update = true;
    }

    if (statusHistoryCreate.getDateAtStatus() != null
        && (!statusHistoryCreate.getDateAtStatus().equals(statusHistory.getDateAtStatus()))) {
      statusHistory.setDateAtStatus(statusHistoryCreate.getDateAtStatus());
      update = true;
    }

    return update;
  }
  /**
   * @param statusHistoryUpdate
   * @param securityContext
   * @return statusHistory
   */
  public StatusHistory updateStatusHistory(
      StatusHistoryUpdate statusHistoryUpdate, SecurityContextBase securityContext) {
    StatusHistory statusHistory = statusHistoryUpdate.getStatusHistory();
    if (updateStatusHistoryNoMerge(statusHistory, statusHistoryUpdate)) {
      this.repository.merge(statusHistory);
    }
    return statusHistory;
  }

  /**
   * @param statusHistoryFilter Object Used to List StatusHistory
   * @param securityContext
   * @return PaginationResponse containing paging information for StatusHistory
   */
  public PaginationResponse<StatusHistory> getAllStatusHistories(
      StatusHistoryFilter statusHistoryFilter, SecurityContextBase securityContext) {
    List<StatusHistory> list = listAllStatusHistories(statusHistoryFilter, securityContext);
    return new PaginationResponse<>(list, statusHistoryFilter.getPageSize(), 0)
            .setTotalRecords(null)
            .setEndPage(null)
            .setTotalPages(null);
  }

  /**
   * @param statusHistoryFilter Object Used to List StatusHistory
   * @param securityContext
   * @return List of StatusHistory
   */
  public List<StatusHistory> listAllStatusHistories(
      StatusHistoryFilter statusHistoryFilter, SecurityContextBase securityContext) {
    return this.repository.listAllStatusHistories(statusHistoryFilter, securityContext);
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

  public PaginationResponse<StatusHistoryContainer> getAllStatusHistoryContainers(StatusHistoryFilter statusHistoryFilter, SecurityContextBase securityContext) {
    List<StatusHistoryContainer> list = listAllStatusHistoryContainers(statusHistoryFilter, securityContext);
    //long count = this.repository.countAllStatusHistories(statusHistoryFilter, securityContext);
    return new PaginationResponse<>(list, statusHistoryFilter.getPageSize(), 0)
            .setTotalRecords(null)
            .setEndPage(null)
            .setTotalPages(null);
  }

  private List<StatusHistoryContainer> listAllStatusHistoryContainers(StatusHistoryFilter statusHistoryFilter, SecurityContextBase securityContext) {
    return repository.listAllStatusHistoryContainers(statusHistoryFilter, securityContext);
  }
}
