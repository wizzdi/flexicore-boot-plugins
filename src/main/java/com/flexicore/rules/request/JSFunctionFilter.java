package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class JSFunctionFilter extends PaginationFilter {


  private Set<String> returnType;

  private Set<String> methodName;

  private BasicPropertiesFilter basicPropertiesFilter;


  public Set<String> getReturnType() {
    return this.returnType;
  }

  public <T extends JSFunctionFilter> T setReturnType(Set<String> returnType) {
    this.returnType = returnType;
    return (T) this;
  }

  public Set<String> getMethodName() {
    return this.methodName;
  }

  public <T extends JSFunctionFilter> T setMethodName(Set<String> methodName) {
    this.methodName = methodName;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends JSFunctionFilter> T setBasicPropertiesFilter(
          BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
