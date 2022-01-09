package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JsFunction;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class JSFunctionParameterCreate extends BasicCreate {

  private Integer ordinal;

  private String jsFunctionId;

  private String parameterType;

  @JsonIgnore private JsFunction jsFunction;

  public Integer getOrdinal() {
    return this.ordinal;
  }

  public <T extends JSFunctionParameterCreate> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public String getJsFunctionId() {
    return this.jsFunctionId;
  }

  public <T extends JSFunctionParameterCreate> T setJsFunctionId(String jsFunctionId) {
    this.jsFunctionId = jsFunctionId;
    return (T) this;
  }

  public String getParameterType() {
    return this.parameterType;
  }

  public <T extends JSFunctionParameterCreate> T setParameterType(String parameterType) {
    this.parameterType = parameterType;
    return (T) this;
  }

  @JsonIgnore
  public JsFunction getJsFunction() {
    return this.jsFunction;
  }

  public <T extends JSFunctionParameterCreate> T setJsFunction(JsFunction jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }
}
