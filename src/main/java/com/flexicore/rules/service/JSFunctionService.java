package com.flexicore.rules.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.rules.data.JSFunctionRepository;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.request.JSFunctionCreate;
import com.flexicore.rules.request.JSFunctionFilter;
import com.flexicore.rules.request.JSFunctionUpdate;
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
public class JSFunctionService implements Plugin {

  @Autowired private JSFunctionRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param jSFunctionCreate Object Used to Create JSFunction
   * @param securityContext
   * @return created JSFunction
   */
  public JSFunction createJSFunction(
          JSFunctionCreate jSFunctionCreate, SecurityContextBase securityContext) {
    JSFunction jSFunction = createJSFunctionNoMerge(jSFunctionCreate, securityContext);
    this.repository.merge(jSFunction);
    return jSFunction;
  }

  /**
   * @param jSFunctionCreate Object Used to Create JSFunction
   * @param securityContext
   * @return created JSFunction unmerged
   */
  public JSFunction createJSFunctionNoMerge(
          JSFunctionCreate jSFunctionCreate, SecurityContextBase securityContext) {
    JSFunction jSFunction = new JSFunction();
    jSFunction.setId(UUID.randomUUID().toString());
    updateJSFunctionNoMerge(jSFunction, jSFunctionCreate);

    BaseclassService.createSecurityObjectNoMerge(jSFunction, securityContext);

    return jSFunction;
  }

  /**
   * @param jSFunctionCreate Object Used to Create JSFunction
   * @param jSFunction
   * @return if jSFunction was updated
   */
  public boolean updateJSFunctionNoMerge(JSFunction jSFunction, JSFunctionCreate jSFunctionCreate) {
    boolean update = basicService.updateBasicNoMerge(jSFunctionCreate, jSFunction);

    if (jSFunctionCreate.getEvaluatingJSCode() != null
            && (jSFunction.getEvaluatingJSCode() == null
            || !jSFunctionCreate
            .getEvaluatingJSCode()
            .getId()
            .equals(jSFunction.getEvaluatingJSCode().getId()))) {
      jSFunction.setEvaluatingJSCode(jSFunctionCreate.getEvaluatingJSCode());
      update = true;
    }

    return update;
  }
  /**
   * @param jSFunctionUpdate
   * @param securityContext
   * @return jSFunction
   */
  public JSFunction updateJSFunction(
          JSFunctionUpdate jSFunctionUpdate, SecurityContextBase securityContext) {
    JSFunction jSFunction = jSFunctionUpdate.getJSFunction();
    if (updateJSFunctionNoMerge(jSFunction, jSFunctionUpdate)) {
      this.repository.merge(jSFunction);
    }
    return jSFunction;
  }

  /**
   * @param jSFunctionFilter Object Used to List JSFunction
   * @param securityContext
   * @return PaginationResponse containing paging information for JSFunction
   */
  public PaginationResponse<JSFunction> getAllJSFunctions(
          JSFunctionFilter jSFunctionFilter, SecurityContextBase securityContext) {
    List<JSFunction> list = listAllJSFunctions(jSFunctionFilter, securityContext);
    long count = this.repository.countAllJSFunctions(jSFunctionFilter, securityContext);
    return new PaginationResponse<>(list, jSFunctionFilter, count);
  }

  /**
   * @param jSFunctionFilter Object Used to List JSFunction
   * @param securityContext
   * @return List of JSFunction
   */
  public List<JSFunction> listAllJSFunctions(
          JSFunctionFilter jSFunctionFilter, SecurityContextBase securityContext) {
    return this.repository.listAllJSFunctions(jSFunctionFilter, securityContext);
  }

  /**
   * @param jSFunctionFilter Object Used to List JSFunction
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionFilter is not valid
   */
  public void validate(JSFunctionFilter jSFunctionFilter, SecurityContextBase securityContext) {
    basicService.validate(jSFunctionFilter, securityContext);


  }

  /**
   * @param jSFunctionCreate Object Used to Create JSFunction
   * @param securityContext
   * @throws ResponseStatusException if jSFunctionCreate is not valid
   */
  public void validate(JSFunctionCreate jSFunctionCreate, SecurityContextBase securityContext) {
    basicService.validate(jSFunctionCreate, securityContext);

    String evaluatingJSCodeId = jSFunctionCreate.getEvaluatingJSCodeId();
    FileResource evaluatingJSCode =
            evaluatingJSCodeId == null
                    ? null
                    : this.repository.getByIdOrNull(
                    evaluatingJSCodeId, FileResource.class, SecuredBasic_.security, securityContext);
    if (evaluatingJSCodeId != null && evaluatingJSCode == null) {
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST, "No FileResource with id " + evaluatingJSCodeId);
    }
    jSFunctionCreate.setEvaluatingJSCode(evaluatingJSCode);
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
