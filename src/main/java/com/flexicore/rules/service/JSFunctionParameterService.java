package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.JSFunctionParameterRepository;
import com.flexicore.rules.model.JSFunctionParameter;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.request.JSFunctionParameterCreate;
import com.flexicore.rules.request.JSFunctionParameterFilter;
import com.flexicore.rules.request.JSFunctionParameterUpdate;
import com.flexicore.security.SecurityContextBase;
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
import javax.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class JSFunctionParameterService implements Plugin, IJSFunctionParameterService {

  @Autowired private JSFunctionParameterRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JSFunctionParameter
   */
  @Override
  public JSFunctionParameter createJSFunctionParameter(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContextBase securityContext) {
    JSFunctionParameter jSFunctionParameter =
        createJSFunctionParameterNoMerge(jSFunctionParameterCreate, securityContext);
    this.repository.merge(jSFunctionParameter);
    return jSFunctionParameter;
  }

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JSFunctionParameter unmerged
   */
  @Override
  public JSFunctionParameter createJSFunctionParameterNoMerge(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContextBase securityContext) {
    JSFunctionParameter jSFunctionParameter = new JSFunctionParameter();
    jSFunctionParameter.setId(UUID.randomUUID().toString());
    updateJSFunctionParameterNoMerge(jSFunctionParameter, jSFunctionParameterCreate);

    BaseclassService.createSecurityObjectNoMerge(jSFunctionParameter, securityContext);

    return jSFunctionParameter;
  }

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param jSFunctionParameter
   * @return if jSFunctionParameter was updated
   */
  @Override
  public boolean updateJSFunctionParameterNoMerge(
      JSFunctionParameter jSFunctionParameter,
      JSFunctionParameterCreate jSFunctionParameterCreate) {
    boolean update =
        basicService.updateBasicNoMerge(jSFunctionParameterCreate, jSFunctionParameter);

    if (jSFunctionParameterCreate.getJsFunction() != null
        && (jSFunctionParameter.getJsFunction() == null
            || !jSFunctionParameterCreate
                .getJsFunction()
                .getId()
                .equals(jSFunctionParameter.getJsFunction().getId()))) {
      jSFunctionParameter.setJsFunction(jSFunctionParameterCreate.getJsFunction());
      update = true;
    }

    if (jSFunctionParameterCreate.getParameterType() != null
        && (!jSFunctionParameterCreate
            .getParameterType()
            .equals(jSFunctionParameter.getParameterType()))) {
      jSFunctionParameter.setParameterType(jSFunctionParameterCreate.getParameterType());
      update = true;
    }

    if (jSFunctionParameterCreate.getOrdinal() != null
        && (!jSFunctionParameterCreate.getOrdinal().equals(jSFunctionParameter.getOrdinal()))) {
      jSFunctionParameter.setOrdinal(jSFunctionParameterCreate.getOrdinal());
      update = true;
    }

    return update;
  }
  /**
   * @param jSFunctionParameterUpdate
   * @param securityContext
   * @return jSFunctionParameter
   */
  @Override
  public JSFunctionParameter updateJSFunctionParameter(
      JSFunctionParameterUpdate jSFunctionParameterUpdate, SecurityContextBase securityContext) {
    JSFunctionParameter jSFunctionParameter = jSFunctionParameterUpdate.getJSFunctionParameter();
    if (updateJSFunctionParameterNoMerge(jSFunctionParameter, jSFunctionParameterUpdate)) {
      this.repository.merge(jSFunctionParameter);
    }
    return jSFunctionParameter;
  }

  /**
   * @param jSFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return PaginationResponse containing paging information for JSFunctionParameter
   */
  @Override
  public PaginationResponse<JSFunctionParameter> getAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContextBase securityContext) {
    List<JSFunctionParameter> list =
        listAllJSFunctionParameters(jSFunctionParameterFilter, securityContext);
    long count =
        this.repository.countAllJSFunctionParameters(jSFunctionParameterFilter, securityContext);
    return new PaginationResponse<>(list, jSFunctionParameterFilter, count);
  }

  /**
   * @param jSFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return List of JSFunctionParameter
   */
  @Override
  public List<JSFunctionParameter> listAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContextBase securityContext) {
    return this.repository.listAllJSFunctionParameters(jSFunctionParameterFilter, securityContext);
  }

  /**
   * @param jSFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionParameterFilter is not valid
   */
  @Override
  public void validate(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContextBase securityContext) {
    basicService.validate(jSFunctionParameterFilter, securityContext);

    Set<String> jsFunctionIds =
        jSFunctionParameterFilter.getJsFunctionIds() == null
            ? new HashSet<>()
            : jSFunctionParameterFilter.getJsFunctionIds();
    Map<String, JsFunction> jsFunction =
        jsFunctionIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(JsFunction.class, jsFunctionIds, SecuredBasic_.security, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    jsFunctionIds.removeAll(jsFunction.keySet());
    if (!jsFunctionIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JsFunction with ids " + jsFunctionIds);
    }
    jSFunctionParameterFilter.setJsFunction(new ArrayList<>(jsFunction.values()));
  }

  /**
   * @param jSFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionParameterCreate is not valid
   */
  @Override
  public void validate(
      JSFunctionParameterCreate jSFunctionParameterCreate, SecurityContextBase securityContext) {
    basicService.validate(jSFunctionParameterCreate, securityContext);

    String jsFunctionId = jSFunctionParameterCreate.getJsFunctionId();
    JsFunction jsFunction =
        jsFunctionId == null
            ? null
            : this.repository.getByIdOrNull(
                jsFunctionId, JsFunction.class, SecuredBasic_.security, securityContext);
    if (jsFunctionId != null && jsFunction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JsFunction with id " + jsFunctionId);
    }
    jSFunctionParameterCreate.setJsFunction(jsFunction);
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  @Override
  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  @Override
  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  @Override
  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  @Override
  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
