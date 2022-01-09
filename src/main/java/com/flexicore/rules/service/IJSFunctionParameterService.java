package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.JSFunctionParameter;
import com.flexicore.rules.request.JSFunctionParameterCreate;
import com.flexicore.rules.request.JSFunctionParameterFilter;
import com.flexicore.rules.request.JSFunctionParameterUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IJSFunctionParameterService {

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JSFunctionParameter
   */
  JSFunctionParameter createJSFunctionParameter(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContextBase securityContext);

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JSFunctionParameter unmerged
   */
  JSFunctionParameter createJSFunctionParameterNoMerge(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContextBase securityContext);

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param jSFunctionParameter
   * @return if jSFunctionParameter was updated
   */
  boolean updateJSFunctionParameterNoMerge(
      JSFunctionParameter jSFunctionParameter, JSFunctionParameterCreate jSFunctionParameterCreate);

  /**
   * @param jSFunctionParameterUpdate
   * @param securityContext
   * @return jSFunctionParameter
   */
  JSFunctionParameter updateJSFunctionParameter(
      JSFunctionParameterUpdate jSFunctionParameterUpdate, SecurityContextBase securityContext);

  /**
   * @param jSFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return PaginationResponse containing paging information for JSFunctionParameter
   */
  PaginationResponse<JSFunctionParameter> getAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContextBase securityContext);

  /**
   * @param jSFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return List of JSFunctionParameter
   */
  List<JSFunctionParameter> listAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContextBase securityContext);

  /**
   * @param jSFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionParameterFilter is not valid
   */
  void validate(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContextBase securityContext);

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionParameterCreate is not valid
   */
  void validate(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContextBase securityContext);

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
