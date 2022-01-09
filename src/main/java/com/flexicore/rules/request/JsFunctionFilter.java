package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class JsFunctionFilter extends PaginationFilter {

  private Set<String> evaluatingJSCodeIds;

  @JsonIgnore private List<FileResource> evaluatingJSCode;

  private Set<String> returnType;

  private Set<String> methodName;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<String> getEvaluatingJSCodeIds() {
    return this.evaluatingJSCodeIds;
  }

  public <T extends JsFunctionFilter> T setEvaluatingJSCodeIds(Set<String> evaluatingJSCodeIds) {
    this.evaluatingJSCodeIds = evaluatingJSCodeIds;
    return (T) this;
  }

  @JsonIgnore
  public List<FileResource> getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  public <T extends JsFunctionFilter> T setEvaluatingJSCode(List<FileResource> evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  public Set<String> getReturnType() {
    return this.returnType;
  }

  public <T extends JsFunctionFilter> T setReturnType(Set<String> returnType) {
    this.returnType = returnType;
    return (T) this;
  }

  public Set<String> getMethodName() {
    return this.methodName;
  }

  public <T extends JsFunctionFilter> T setMethodName(Set<String> methodName) {
    this.methodName = methodName;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends JsFunctionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
