package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.request.JSFunctionCreate;
import com.flexicore.rules.request.JSFunctionFilter;
import com.flexicore.rules.request.JSFunctionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IJSFunctionService {

  /**
   * @param jSFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @return created JSFunction
   */
  JSFunction createJSFunction(
      JSFunctionCreate jSFunctionCreate, SecurityContextBase securityContext);

  /**
   * @param jSFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @return created JSFunction unmerged
   */
  JSFunction createJSFunctionNoMerge(
      JSFunctionCreate jSFunctionCreate, SecurityContextBase securityContext);

  /**
   * @param jSFunctionCreate Object Used to Create JsFunction
   * @param jSFunction
   * @return if jSFunction was updated
   */
  boolean updateJSFunctionNoMerge(JSFunction jSFunction, JSFunctionCreate jSFunctionCreate);

  /**
   * @param jSFunctionUpdate
   * @param securityContext
   * @return jSFunction
   */
  JSFunction updateJSFunction(
      JSFunctionUpdate jSFunctionUpdate, SecurityContextBase securityContext);

  /**
   * @param jSFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @return PaginationResponse containing paging information for JSFunction
   */
  PaginationResponse<JSFunction> getAllJSFunctions(
      JSFunctionFilter jSFunctionFilter, SecurityContextBase securityContext);

  /**
   * @param jSFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @return List of JSFunction
   */
  List<JSFunction> listAllJSFunctions(
      JSFunctionFilter jSFunctionFilter, SecurityContextBase securityContext);

  /**
   * @param jSFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionFilter is not valid
   */
  void validate(JSFunctionFilter jSFunctionFilter, SecurityContextBase securityContext);

  /**
   * @param jSFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionCreate is not valid
   */
  void validate(JSFunctionCreate jSFunctionCreate, SecurityContextBase securityContext);

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
