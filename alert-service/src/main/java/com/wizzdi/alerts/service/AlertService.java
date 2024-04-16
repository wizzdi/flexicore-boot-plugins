package com.wizzdi.alerts.service;

import com.wizzdi.alerts.Alert;
import com.wizzdi.alerts.data.AlertRepository;
import com.wizzdi.alerts.request.AlertCreate;
import com.wizzdi.alerts.request.AlertFilter;
import com.wizzdi.alerts.request.AlertUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class AlertService implements Plugin {

  @Autowired private AlertRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param alertCreate Object Used to Create Alert
   * @param securityContext
   * @return created Alert
   */
  public Alert createAlert(AlertCreate alertCreate, SecurityContextBase securityContext) {
    Alert alert = createAlertNoMerge(alertCreate, securityContext);
    this.repository.merge(alert);
    return alert;
  }

  /**
   * @param alertCreate Object Used to Create Alert
   * @param securityContext
   * @return created Alert unmerged
   */
  public Alert createAlertNoMerge(AlertCreate alertCreate, SecurityContextBase securityContext) {
    Alert alert = new Alert();
    alert.setId(UUID.randomUUID().toString());
    updateAlertNoMerge(alert, alertCreate);

    BaseclassService.createSecurityObjectNoMerge(alert, securityContext);

    return alert;
  }

  /**
   * @param alertCreate Object Used to Create Alert
   * @param alert
   * @return if alert was updated
   */
  public boolean updateAlertNoMerge(Alert alert, AlertCreate alertCreate) {
    boolean update = basicService.updateBasicNoMerge(alertCreate, alert);

    if (alertCreate.getAlertCategory() != null
        && (!alertCreate.getAlertCategory().equals(alert.getAlertCategory()))) {
      alert.setAlertCategory(alertCreate.getAlertCategory());
      update = true;
    }

    if (alertCreate.getAlertLevel() != null
        && (!alertCreate.getAlertLevel().equals(alert.getAlertLevel()))) {
      alert.setAlertLevel(alertCreate.getAlertLevel());
      update = true;
    }

    if (alertCreate.getAlertContent() != null
        && (!alertCreate.getAlertContent().equals(alert.getAlertContent()))) {
      alert.setAlertContent(alertCreate.getAlertContent());
      update = true;
    }

    if (alertCreate.getRelatedId() != null
        && (!alertCreate.getRelatedId().equals(alert.getRelatedId()))) {
      alert.setRelatedId(alertCreate.getRelatedId());
      update = true;
    }

    if (alertCreate.getRelatedType() != null
        && (!alertCreate.getRelatedType().equals(alert.getRelatedType()))) {
      alert.setRelatedType(alertCreate.getRelatedType());
      update = true;
    }
    if(alertCreate.getHandledAt()!=null&&!alertCreate.getHandledAt().equals(alert.getHandledAt())){
      alert.setHandledAt(alertCreate.getHandledAt());
      update=true;
    }

    return update;
  }

  /**
   * @param alertUpdate
   * @param securityContext
   * @return alert
   */
  public Alert updateAlert(AlertUpdate alertUpdate, SecurityContextBase securityContext) {
    Alert alert = alertUpdate.getAlert();
    if (updateAlertNoMerge(alert, alertUpdate)) {
      this.repository.merge(alert);
    }
    return alert;
  }

  /**
   * @param alertFilter Object Used to List Alert
   * @param securityContext
   * @return PaginationResponse containing paging information for Alert
   */
  public PaginationResponse<Alert> getAllAlerts(
          AlertFilter alertFilter, SecurityContextBase securityContext) {
    List<Alert> list = listAllAlerts(alertFilter, securityContext);
    long count = this.repository.countAllAlerts(alertFilter, securityContext);
    return new PaginationResponse<>(list, alertFilter.getPageSize(), count);
  }

  /**
   * @param alertFilter Object Used to List Alert
   * @param securityContext
   * @return List of Alert
   */
  public List<Alert> listAllAlerts(AlertFilter alertFilter, SecurityContextBase securityContext) {
    return this.repository.listAllAlerts(alertFilter, securityContext);
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
