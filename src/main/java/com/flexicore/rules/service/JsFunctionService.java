package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.JsFunctionRepository;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.request.JsFunctionCreate;
import com.flexicore.rules.request.JsFunctionFilter;
import com.flexicore.rules.request.JsFunctionUpdate;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
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
public class JsFunctionService implements Plugin, IJsFunctionService {

  @Autowired private JsFunctionRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @return created JsFunction
   */
  @Override
  public JsFunction createJsFunction(
      JsFunctionCreate jsFunctionCreate, SecurityContextBase securityContext) {
    JsFunction jsFunction = createJsFunctionNoMerge(jsFunctionCreate, securityContext);
    this.repository.merge(jsFunction);
    return jsFunction;
  }

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @return created JsFunction unmerged
   */
  @Override
  public JsFunction createJsFunctionNoMerge(
      JsFunctionCreate jsFunctionCreate, SecurityContextBase securityContext) {
    JsFunction jsFunction = new JsFunction();
    jsFunction.setId(UUID.randomUUID().toString());
    updateJsFunctionNoMerge(jsFunction, jsFunctionCreate);

    BaseclassService.createSecurityObjectNoMerge(jsFunction, securityContext);

    return jsFunction;
  }

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param jsFunction
   * @return if jsFunction was updated
   */
  @Override
  public boolean updateJsFunctionNoMerge(JsFunction jsFunction, JsFunctionCreate jsFunctionCreate) {
    boolean update = basicService.updateBasicNoMerge(jsFunctionCreate, jsFunction);

    if (jsFunctionCreate.getEvaluatingJSCode() != null
        && (jsFunction.getEvaluatingJSCode() == null
            || !jsFunctionCreate
                .getEvaluatingJSCode()
                .getId()
                .equals(jsFunction.getEvaluatingJSCode().getId()))) {
      jsFunction.setEvaluatingJSCode(jsFunctionCreate.getEvaluatingJSCode());
      update = true;
    }

    if (jsFunctionCreate.getMethodName() != null
        && (!jsFunctionCreate.getMethodName().equals(jsFunction.getMethodName()))) {
      jsFunction.setMethodName(jsFunctionCreate.getMethodName());
      update = true;
    }

    if (jsFunctionCreate.getReturnType() != null
        && (!jsFunctionCreate.getReturnType().equals(jsFunction.getReturnType()))) {
      jsFunction.setReturnType(jsFunctionCreate.getReturnType());
      update = true;
    }

    return update;
  }
  /**
   * @param jsFunctionUpdate
   * @param securityContext
   * @return jsFunction
   */
  @Override
  public JsFunction updateJsFunction(
      JsFunctionUpdate jsFunctionUpdate, SecurityContextBase securityContext) {
    JsFunction jsFunction = jsFunctionUpdate.getJsFunction();
    if (updateJsFunctionNoMerge(jsFunction, jsFunctionUpdate)) {
      this.repository.merge(jsFunction);
    }
    return jsFunction;
  }

  /**
   * @param jsFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @return PaginationResponse containing paging information for JsFunction
   */
  @Override
  public PaginationResponse<JsFunction> getAllJsFunctions(
      JsFunctionFilter jsFunctionFilter, SecurityContextBase securityContext) {
    List<JsFunction> list = listAllJsFunctions(jsFunctionFilter, securityContext);
    long count = this.repository.countAllJsFunctions(jsFunctionFilter, securityContext);
    return new PaginationResponse<>(list, jsFunctionFilter, count);
  }

  /**
   * @param jsFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @return List of JsFunction
   */
  @Override
  public List<JsFunction> listAllJsFunctions(
      JsFunctionFilter jsFunctionFilter, SecurityContextBase securityContext) {
    return this.repository.listAllJsFunctions(jsFunctionFilter, securityContext);
  }

  /**
   * @param jsFunctionFilter Object Used to List JsFunction
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionFilter is not valid
   */
  @Override
  public void validate(JsFunctionFilter jsFunctionFilter, SecurityContextBase securityContext) {
    basicService.validate(jsFunctionFilter, securityContext);

    Set<String> evaluatingJSCodeIds =
        jsFunctionFilter.getEvaluatingJSCodeIds() == null
            ? new HashSet<>()
            : jsFunctionFilter.getEvaluatingJSCodeIds();
    Map<String, FileResource> evaluatingJSCode =
        evaluatingJSCodeIds.isEmpty()
            ? new HashMap<>()
            : this.repository
                .listByIds(
                    FileResource.class,
                    evaluatingJSCodeIds,
                    SecuredBasic_.security,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    evaluatingJSCodeIds.removeAll(evaluatingJSCode.keySet());
    if (!evaluatingJSCodeIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No FileResource with ids " + evaluatingJSCodeIds);
    }
    jsFunctionFilter.setEvaluatingJSCode(new ArrayList<>(evaluatingJSCode.values()));
  }

  /**
   * @param jsFunctionCreate Object Used to Create JsFunction
   * @param securityContext
   * @throws ResponseStatusException if jsFunctionCreate is not valid
   */
  @Override
  public void validate(JsFunctionCreate jsFunctionCreate, SecurityContextBase securityContext) {
    basicService.validate(jsFunctionCreate, securityContext);

    String evaluatingJSCodeId = jsFunctionCreate.getEvaluatingJSCodeId();
    FileResource evaluatingJSCode =
        evaluatingJSCodeId == null
            ? null
            : this.repository.getByIdOrNull(
                evaluatingJSCodeId, FileResource.class, SecuredBasic_.security, securityContext);
    if (evaluatingJSCodeId != null && evaluatingJSCode == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No FileResource with id " + evaluatingJSCodeId);
    }
    jsFunctionCreate.setEvaluatingJSCode(evaluatingJSCode);
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
