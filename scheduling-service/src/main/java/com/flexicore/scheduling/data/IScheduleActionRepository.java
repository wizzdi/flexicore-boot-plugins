package com.flexicore.scheduling.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.scheduling.model.ScheduleAction;
import com.flexicore.scheduling.request.ScheduleActionFilter;
import com.flexicore.security.SecurityContextBase;
import java.util.List;
import java.util.Set;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;

public interface IScheduleActionRepository {

  /**
   * @param filtering Object Used to List ScheduleAction
   * @param securityContext
   * @return List of ScheduleAction
   */
  List<ScheduleAction> listAllScheduleActions(
      ScheduleActionFilter filtering, SecurityContextBase securityContext);

  <T extends ScheduleAction> void addScheduleActionPredicate(
      ScheduleActionFilter filtering,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext);

  /**
   * @param filtering Object Used to List ScheduleAction
   * @param securityContext
   * @return count of ScheduleAction
   */
  Long countAllScheduleActions(ScheduleActionFilter filtering, SecurityContextBase securityContext);

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
