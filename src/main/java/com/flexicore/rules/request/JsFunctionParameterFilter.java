package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JsFunction;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class JsFunctionParameterFilter extends PaginationFilter {

  private Set<Integer> ordinal;

  private Set<String> jsFunctionIds;

  private Set<String> parameterType;

  @JsonIgnore private List<JsFunction> jsFunction;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<Integer> getOrdinal() {
    return this.ordinal;
  }

  public <T extends JsFunctionParameterFilter> T setOrdinal(Set<Integer> ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public Set<String> getJsFunctionIds() {
    return this.jsFunctionIds;
  }

  public <T extends JsFunctionParameterFilter> T setJsFunctionIds(Set<String> jsFunctionIds) {
    this.jsFunctionIds = jsFunctionIds;
    return (T) this;
  }

  public Set<String> getParameterType() {
    return this.parameterType;
  }

  public <T extends JsFunctionParameterFilter> T setParameterType(Set<String> parameterType) {
    this.parameterType = parameterType;
    return (T) this;
  }

  @JsonIgnore
  public List<JsFunction> getJsFunction() {
    return this.jsFunction;
  }

  public <T extends JsFunctionParameterFilter> T setJsFunction(List<JsFunction> jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends JsFunctionParameterFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
