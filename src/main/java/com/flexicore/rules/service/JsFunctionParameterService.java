package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.JsFunctionParameterRepository;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.model.JsFunctionParameter;
import com.flexicore.rules.request.JsFunctionParameterCreate;
import com.flexicore.rules.request.JsFunctionParameterFilter;
import com.flexicore.rules.request.JsFunctionParameterUpdate;
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
public class JsFunctionParameterService implements Plugin, IJsFunctionParameterService {

  @Autowired private JsFunctionParameterRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JsFunctionParameter
   */
  @Override
  public JsFunctionParameter createJsFunctionParameter(
      JsFunctionParameterCreate jsFunctionParameterCreate, SecurityContextBase securityContext) {
    JsFunctionParameter jsFunctionParameter =
        createJsFunctionParameterNoMerge(jsFunctionParameterCreate, securityContext);
    this.repository.merge(jsFunctionParameter);
    return jsFunctionParameter;
  }

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @return created JsFunctionParameter unmerged
   */
  @Override
  public JsFunctionParameter createJsFunctionParameterNoMerge(
      JsFunctionParameterCreate jsFunctionParameterCreate, SecurityContextBase securityContext) {
    JsFunctionParameter jsFunctionParameter = new JsFunctionParameter();
    jsFunctionParameter.setId(UUID.randomUUID().toString());
    updateJsFunctionParameterNoMerge(jsFunctionParameter, jsFunctionParameterCreate);

    BaseclassService.createSecurityObjectNoMerge(jsFunctionParameter, securityContext);

    return jsFunctionParameter;
  }

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param jsFunctionParameter
   * @return if jsFunctionParameter was updated
   */
  @Override
  public boolean updateJsFunctionParameterNoMerge(
      JsFunctionParameter jsFunctionParameter,
      JsFunctionParameterCreate jsFunctionParameterCreate) {
    boolean update =
        basicService.updateBasicNoMerge(jsFunctionParameterCreate, jsFunctionParameter);

    if (jsFunctionParameterCreate.getJsFunction() != null
        && (jsFunctionParameter.getJsFunction() == null
            || !jsFunctionParameterCreate
                .getJsFunction()
                .getId()
                .equals(jsFunctionParameter.getJsFunction().getId()))) {
      jsFunctionParameter.setJsFunction(jsFunctionParameterCreate.getJsFunction());
      update = true;
    }

    if (jsFunctionParameterCreate.getParameterType() != null
        && (!jsFunctionParameterCreate
            .getParameterType()
            .equals(jsFunctionParameter.getParameterType()))) {
      jsFunctionParameter.setParameterType(jsFunctionParameterCreate.getParameterType());
      update = true;
    }

    if (jsFunctionParameterCreate.getOrdinal() != null
        && (!jsFunctionParameterCreate.getOrdinal().equals(jsFunctionParameter.getOrdinal()))) {
      jsFunctionParameter.setOrdinal(jsFunctionParameterCreate.getOrdinal());
      update = true;
    }

    return update;
  }
  /**
   * @param jsFunctionParameterUpdate
   * @param securityContext
   * @return jsFunctionParameter
   */
  @Override
  public JsFunctionParameter updateJsFunctionParameter(
      JsFunctionParameterUpdate jsFunctionParameterUpdate, SecurityContextBase securityContext) {
    JsFunctionParameter jsFunctionParameter = jsFunctionParameterUpdate.getJsFunctionParameter();
    if (updateJsFunctionParameterNoMerge(jsFunctionParameter, jsFunctionParameterUpdate)) {
      this.repository.merge(jsFunctionParameter);
    }
    return jsFunctionParameter;
  }

  /**
   * @param jsFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return PaginationResponse containing paging information for JsFunctionParameter
   */
  @Override
  public PaginationResponse<JsFunctionParameter> getAllJsFunctionParameters(
      JsFunctionParameterFilter jsFunctionParameterFilter, SecurityContextBase securityContext) {
    List<JsFunctionParameter> list =
        listAllJsFunctionParameters(jsFunctionParameterFilter, securityContext);
    long count =
        this.repository.countAllJsFunctionParameters(jsFunctionParameterFilter, securityContext);
    return new PaginationResponse<>(list, jsFunctionParameterFilter, count);
  }

  /**
   * @param jsFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @return List of JsFunctionParameter
   */
  @Override
  public List<JsFunctionParameter> listAllJsFunctionParameters(
      JsFunctionParameterFilter jsFunctionParameterFilter, SecurityContextBase securityContext) {
    return this.repository.listAllJsFunctionParameters(jsFunctionParameterFilter, securityContext);
  }

  /**
   * @param jsFunctionParameterFilter Object Used to List JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionParameterFilter is not valid
   */
  @Override
  public void validate(
      JsFunctionParameterFilter jsFunctionParameterFilter, SecurityContextBase securityContext) {
    basicService.validate(jsFunctionParameterFilter, securityContext);

    Set<String> jsFunctionIds =
        jsFunctionParameterFilter.getJsFunctionIds() == null
            ? new HashSet<>()
            : jsFunctionParameterFilter.getJsFunctionIds();
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
    jsFunctionParameterFilter.setJsFunction(new ArrayList<>(jsFunction.values()));
  }

  /**
   * @param jsFunctionParameterCreate Object Used to Create JsFunctionParameter
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionParameterCreate is not valid
   */
  @Override
  public void validate(
      JsFunctionParameterCreate jsFunctionParameterCreate, SecurityContextBase securityContext) {
    basicService.validate(jsFunctionParameterCreate, securityContext);

    String jsFunctionId = jsFunctionParameterCreate.getJsFunctionId();
    JsFunction jsFunction =
        jsFunctionId == null
            ? null
            : this.repository.getByIdOrNull(
                jsFunctionId, JsFunction.class, SecuredBasic_.security, securityContext);
    if (jsFunctionId != null && jsFunction == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No JsFunction with id " + jsFunctionId);
    }
    jsFunctionParameterCreate.setJsFunction(jsFunction);
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
