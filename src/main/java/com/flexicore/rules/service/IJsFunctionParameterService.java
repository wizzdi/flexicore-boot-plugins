package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.rules.model.JsFunctionParameter;
import com.flexicore.rules.request.JsFunctionParameterCreate;
import com.flexicore.rules.request.JsFunctionParameterFilter;
import com.flexicore.rules.request.JsFunctionParameterUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IJsFunctionParameterService {

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JsFunctionParameter
   */
  JsFunctionParameter createJsFunctionParameter(
      JsFunctionParameterCreate jsFunctionParameterCreate, SecurityContextBase securityContext);

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JsFunctionParameter unmerged
   */
  JsFunctionParameter createJsFunctionParameterNoMerge(
      JsFunctionParameterCreate jsFunctionParameterCreate, SecurityContextBase securityContext);

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param jsFunctionParameter
   * @return if jsFunctionParameter was updated
   */
  boolean updateJsFunctionParameterNoMerge(
      JsFunctionParameter jsFunctionParameter, JsFunctionParameterCreate jsFunctionParameterCreate);

  /**
   * @param jsFunctionParameterUpdate
   * @param securityContext
   * @return jsFunctionParameter
   */
  JsFunctionParameter updateJsFunctionParameter(
      JsFunctionParameterUpdate jsFunctionParameterUpdate, SecurityContextBase securityContext);

  /**
   * @param jsFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return PaginationResponse containing paging information for JsFunctionParameter
   */
  PaginationResponse<JsFunctionParameter> getAllJsFunctionParameters(
      JsFunctionParameterFilter jsFunctionParameterFilter, SecurityContextBase securityContext);

  /**
   * @param jsFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return List of JsFunctionParameter
   */
  List<JsFunctionParameter> listAllJsFunctionParameters(
      JsFunctionParameterFilter jsFunctionParameterFilter, SecurityContextBase securityContext);

  /**
   * @param jsFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionParameterFilter is not valid
   */
  void validate(
      JsFunctionParameterFilter jsFunctionParameterFilter, SecurityContextBase securityContext);

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionParameterCreate is not valid
   */
  void validate(
      JsFunctionParameterCreate jsFunctionParameterCreate, SecurityContextBase securityContext);

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
