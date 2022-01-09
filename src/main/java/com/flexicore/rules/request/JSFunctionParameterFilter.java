package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JSFunction;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class JSFunctionParameterFilter extends PaginationFilter {

  private Set<Integer> ordinal;

  private Set<String> jsFunctionIds;

  private Set<String> parameterType;

  @JsonIgnore private List<JSFunction> jsFunction;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<Integer> getOrdinal() {
    return this.ordinal;
  }

  public <T extends JSFunctionParameterFilter> T setOrdinal(Set<Integer> ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public Set<String> getJsFunctionIds() {
    return this.jsFunctionIds;
  }

  public <T extends JSFunctionParameterFilter> T setJsFunctionIds(Set<String> jsFunctionIds) {
    this.jsFunctionIds = jsFunctionIds;
    return (T) this;
  }

  public Set<String> getParameterType() {
    return this.parameterType;
  }

  public <T extends JSFunctionParameterFilter> T setParameterType(Set<String> parameterType) {
    this.parameterType = parameterType;
    return (T) this;
  }

  @JsonIgnore
  public List<JSFunction> getJsFunction() {
    return this.jsFunction;
  }

  public <T extends JSFunctionParameterFilter> T setJsFunction(List<JSFunction> jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends JSFunctionParameterFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
