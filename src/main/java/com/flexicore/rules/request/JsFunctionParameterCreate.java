package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JsFunction;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class JsFunctionParameterCreate extends BasicCreate {

  private String ordinal;

  private String jsFunctionId;

  private String parameterType;

  @JsonIgnore private JsFunction jsFunction;

  public String getOrdinal() {
    return this.ordinal;
  }

  public <T extends JsFunctionParameterCreate> T setOrdinal(String ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public String getJsFunctionId() {
    return this.jsFunctionId;
  }

  public <T extends JsFunctionParameterCreate> T setJsFunctionId(String jsFunctionId) {
    this.jsFunctionId = jsFunctionId;
    return (T) this;
  }

  public String getParameterType() {
    return this.parameterType;
  }

  public <T extends JsFunctionParameterCreate> T setParameterType(String parameterType) {
    this.parameterType = parameterType;
    return (T) this;
  }

  @JsonIgnore
  public JsFunction getJsFunction() {
    return this.jsFunction;
  }

  public <T extends JsFunctionParameterCreate> T setJsFunction(JsFunction jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }
}
