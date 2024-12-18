package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;

import com.flexicore.rules.data.JSFunctionParameterRepository;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.model.JSFunctionParameter;
import com.flexicore.rules.request.JSFunctionParameterCreate;
import com.flexicore.rules.request.JSFunctionParameterFilter;
import com.flexicore.rules.request.JSFunctionParameterUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class JSFunctionParameterService implements Plugin {

  @Autowired private JSFunctionParameterRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param jSFunctionParameterCreate Object Used to Create JSFunctionParameter
   * @param securityContext
   * @return created JSFunctionParameter
   */
  public JSFunctionParameter createJSFunctionParameter(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContext securityContext) {
    JSFunctionParameter jSFunctionParameter =
        createJSFunctionParameterNoMerge(jSFunctionParameterCreate, securityContext);
    this.repository.merge(jSFunctionParameter);
    return jSFunctionParameter;
  }

  /**
   * @param jSFunctionParameterCreate Object Used to Create JSFunctionParameter
   * @param securityContext
   * @return created JSFunctionParameter unmerged
   */
  public JSFunctionParameter createJSFunctionParameterNoMerge(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContext securityContext) {
    JSFunctionParameter jSFunctionParameter = new JSFunctionParameter();
    jSFunctionParameter.setId(UUID.randomUUID().toString());
    updateJSFunctionParameterNoMerge(jSFunctionParameter, jSFunctionParameterCreate);

    BaseclassService.createSecurityObjectNoMerge(jSFunctionParameter, securityContext);

    return jSFunctionParameter;
  }

  /**
   * @param jSFunctionParameterCreate Object Used to Create JSFunctionParameter
   * @param jSFunctionParameter
   * @return if jSFunctionParameter was updated
   */
  public boolean updateJSFunctionParameterNoMerge(
      JSFunctionParameter jSFunctionParameter,
      JSFunctionParameterCreate jSFunctionParameterCreate) {
    boolean update =
        basicService.updateBasicNoMerge(jSFunctionParameterCreate, jSFunctionParameter);

    if (jSFunctionParameterCreate.getOrdinal() != null
        && (!jSFunctionParameterCreate.getOrdinal().equals(jSFunctionParameter.getOrdinal()))) {
      jSFunctionParameter.setOrdinal(jSFunctionParameterCreate.getOrdinal());
      update = true;
    }

    if (jSFunctionParameterCreate.getParameterType() != null
        && (!jSFunctionParameterCreate
            .getParameterType()
            .equals(jSFunctionParameter.getParameterType()))) {
      jSFunctionParameter.setParameterType(jSFunctionParameterCreate.getParameterType());
      update = true;
    }

    if (jSFunctionParameterCreate.getJsFunction() != null
        && (jSFunctionParameter.getJsFunction() == null
            || !jSFunctionParameterCreate
                .getJsFunction()
                .getId()
                .equals(jSFunctionParameter.getJsFunction().getId()))) {
      jSFunctionParameter.setJsFunction(jSFunctionParameterCreate.getJsFunction());
      update = true;
    }

    return update;
  }
  /**
   * @param jSFunctionParameterUpdate
   * @param securityContext
   * @return jSFunctionParameter
   */
  public JSFunctionParameter updateJSFunctionParameter(
      JSFunctionParameterUpdate jSFunctionParameterUpdate, SecurityContext securityContext) {
    JSFunctionParameter jSFunctionParameter = jSFunctionParameterUpdate.getJSFunctionParameter();
    if (updateJSFunctionParameterNoMerge(jSFunctionParameter, jSFunctionParameterUpdate)) {
      this.repository.merge(jSFunctionParameter);
    }
    return jSFunctionParameter;
  }

  /**
   * @param jSFunctionParameterFilter Object Used to List JSFunctionParameter
   * @param securityContext
   * @return PaginationResponse containing paging information for JSFunctionParameter
   */
  public PaginationResponse<JSFunctionParameter> getAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContext securityContext) {
    List<JSFunctionParameter> list =
        listAllJSFunctionParameters(jSFunctionParameterFilter, securityContext);
    long count =
        this.repository.countAllJSFunctionParameters(jSFunctionParameterFilter, securityContext);
    return new PaginationResponse<>(list, jSFunctionParameterFilter, count);
  }

  /**
   * @param jSFunctionParameterFilter Object Used to List JSFunctionParameter
   * @param securityContext
   * @return List of JSFunctionParameter
   */
  public List<JSFunctionParameter> listAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContext securityContext) {
    return this.repository.listAllJSFunctionParameters(jSFunctionParameterFilter, securityContext);
  }

  /**
   * @param jSFunctionParameterFilter Object Used to List JSFunctionParameter
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if jSFunctionParameterFilter is not valid
   */
  public void validate(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContext securityContext) {
    basicService.validate(jSFunctionParameterFilter, securityContext);

    Set<String> jsFunctionIds =
        jSFunctionParameterFilter.getJsFunctionIds() == null
            ? new HashSet<>()
            : jSFunctionParameterFilter.getJsFunctionIds();
    Map<String, JSFunction> jsFunction =
        jsFunctionIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(JSFunction.class, jsFunctionIds,securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    jsFunctionIds.removeAll(jsFunction.keySet());
    if (!jsFunctionIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Set with ids " + jsFunctionIds);
    }
    jSFunctionParameterFilter.setJsFunction(new ArrayList<>(jsFunction.values()));
  }

  /**
   * @param jSFunctionParameterCreate Object Used to Create JSFunctionParameter
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if jSFunctionParameterCreate is not valid
   */
  public void validate(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContext securityContext) {
    basicService.validate(jSFunctionParameterCreate, securityContext);

    String jsFunctionId = jSFunctionParameterCreate.getJsFunctionId();
    JSFunction jsFunction =
        jsFunctionId == null
            ? null
            : this.repository.getByIdOrNull(
                jsFunctionId, JSFunction.class,  securityContext);
    if (jsFunctionId != null && jsFunction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JSFunction with id " + jsFunctionId);
    }
    jSFunctionParameterCreate.setJsFunction(jsFunction);
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

  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
