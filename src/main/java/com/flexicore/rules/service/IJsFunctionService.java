package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.request.JsFunctionCreate;
import com.flexicore.rules.request.JsFunctionFilter;
import com.flexicore.rules.request.JsFunctionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IJsFunctionService {

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @return created JsFunction
   */
  JsFunction createJsFunction(
      JsFunctionCreate jsFunctionCreate, SecurityContextBase securityContext);

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @return created JsFunction unmerged
   */
  JsFunction createJsFunctionNoMerge(
      JsFunctionCreate jsFunctionCreate, SecurityContextBase securityContext);

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param jsFunction
   * @return if jsFunction was updated
   */
  boolean updateJsFunctionNoMerge(JsFunction jsFunction, JsFunctionCreate jsFunctionCreate);

  /**
   * @param jsFunctionUpdate
   * @param securityContext
   * @return jsFunction
   */
  JsFunction updateJsFunction(
      JsFunctionUpdate jsFunctionUpdate, SecurityContextBase securityContext);

  /**
   * @param jsFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @return PaginationResponse containing paging information for JsFunction
   */
  PaginationResponse<JsFunction> getAllJsFunctions(
      JsFunctionFilter jsFunctionFilter, SecurityContextBase securityContext);

  /**
   * @param jsFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @return List of JsFunction
   */
  List<JsFunction> listAllJsFunctions(
      JsFunctionFilter jsFunctionFilter, SecurityContextBase securityContext);

  /**
   * @param jsFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionFilter is not valid
   */
  void validate(JsFunctionFilter jsFunctionFilter, SecurityContextBase securityContext);

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionCreate is not valid
   */
  void validate(JsFunctionCreate jsFunctionCreate, SecurityContextBase securityContext);

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
