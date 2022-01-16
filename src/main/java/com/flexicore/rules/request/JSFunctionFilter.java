package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List JSFunction */
public class JSFunctionFilter extends PaginationFilter {

  @JsonIgnore private List<FileResource> evaluatingJSCode;

  private Set<String> returnType;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> methodName;

  private Set<String> evaluatingJSCodeIds;

  /** @return evaluatingJSCode */
  @JsonIgnore
  public List<FileResource> getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return JSFunctionFilter
   */
  public <T extends JSFunctionFilter> T setEvaluatingJSCode(List<FileResource> evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  /** @return returnType */
  public Set<String> getReturnType() {
    return this.returnType;
  }

  /**
   * @param returnType returnType to set
   * @return JSFunctionFilter
   */
  public <T extends JSFunctionFilter> T setReturnType(Set<String> returnType) {
    this.returnType = returnType;
    return (T) this;
  }

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return JSFunctionFilter
   */
  public <T extends JSFunctionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return methodName */
  public Set<String> getMethodName() {
    return this.methodName;
  }

  /**
   * @param methodName methodName to set
   * @return JSFunctionFilter
   */
  public <T extends JSFunctionFilter> T setMethodName(Set<String> methodName) {
    this.methodName = methodName;
    return (T) this;
  }

  /** @return evaluatingJSCodeIds */
  public Set<String> getEvaluatingJSCodeIds() {
    return this.evaluatingJSCodeIds;
  }

  /**
   * @param evaluatingJSCodeIds evaluatingJSCodeIds to set
   * @return JSFunctionFilter
   */
  public <T extends JSFunctionFilter> T setEvaluatingJSCodeIds(Set<String> evaluatingJSCodeIds) {
    this.evaluatingJSCodeIds = evaluatingJSCodeIds;
    return (T) this;
  }
}
