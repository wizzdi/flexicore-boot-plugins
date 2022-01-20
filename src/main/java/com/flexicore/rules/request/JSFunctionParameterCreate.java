package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JSFunction;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create JSFunctionParameter */
public class JSFunctionParameterCreate extends BasicCreate {

  private String parameterType;

  @JsonIgnore private JSFunction jsFunction;

  private String jsFunctionId;

  private Integer ordinal;

  /** @return parameterType */
  public String getParameterType() {
    return this.parameterType;
  }

  /**
   * @param parameterType parameterType to set
   * @return JSFunctionParameterCreate
   */
  public <T extends JSFunctionParameterCreate> T setParameterType(String parameterType) {
    this.parameterType = parameterType;
    return (T) this;
  }

  /** @return jsFunction */
  @JsonIgnore
  public JSFunction getJsFunction() {
    return this.jsFunction;
  }

  /**
   * @param jsFunction jsFunction to set
   * @return JSFunctionParameterCreate
   */
  public <T extends JSFunctionParameterCreate> T setJsFunction(JSFunction jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }

  /** @return jsFunctionId */
  public String getJsFunctionId() {
    return this.jsFunctionId;
  }

  /**
   * @param jsFunctionId jsFunctionId to set
   * @return JSFunctionParameterCreate
   */
  public <T extends JSFunctionParameterCreate> T setJsFunctionId(String jsFunctionId) {
    this.jsFunctionId = jsFunctionId;
    return (T) this;
  }

  /** @return ordinal */
  public Integer getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return JSFunctionParameterCreate
   */
  public <T extends JSFunctionParameterCreate> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }
}
