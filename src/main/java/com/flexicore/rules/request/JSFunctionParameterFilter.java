package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JSFunction;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List JSFunctionParameter */
public class JSFunctionParameterFilter extends PaginationFilter {

  private Set<String> jsFunctionIds;

  private Set<Integer> ordinal;

  @JsonIgnore private List<JSFunction> jsFunction;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> parameterType;

  /** @return jsFunctionIds */
  public Set<String> getJsFunctionIds() {
    return this.jsFunctionIds;
  }

  /**
   * @param jsFunctionIds jsFunctionIds to set
   * @return JSFunctionParameterFilter
   */
  public <T extends JSFunctionParameterFilter> T setJsFunctionIds(Set<String> jsFunctionIds) {
    this.jsFunctionIds = jsFunctionIds;
    return (T) this;
  }

  /** @return ordinal */
  public Set<Integer> getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return JSFunctionParameterFilter
   */
  public <T extends JSFunctionParameterFilter> T setOrdinal(Set<Integer> ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return jsFunction */
  @JsonIgnore
  public List<JSFunction> getJsFunction() {
    return this.jsFunction;
  }

  /**
   * @param jsFunction jsFunction to set
   * @return JSFunctionParameterFilter
   */
  public <T extends JSFunctionParameterFilter> T setJsFunction(List<JSFunction> jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return JSFunctionParameterFilter
   */
  public <T extends JSFunctionParameterFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return parameterType */
  public Set<String> getParameterType() {
    return this.parameterType;
  }

  /**
   * @param parameterType parameterType to set
   * @return JSFunctionParameterFilter
   */
  public <T extends JSFunctionParameterFilter> T setParameterType(Set<String> parameterType) {
    this.parameterType = parameterType;
    return (T) this;
  }
}
