package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JSFunctionParameter;

public class JSFunctionParameterUpdate extends JSFunctionParameterCreate {

  private String id;
  @JsonIgnore private JSFunctionParameter jSFunctionParameter;

  public String getId() {
    return id;
  }

  public <T extends JSFunctionParameterUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public JSFunctionParameter getJSFunctionParameter() {
    return jSFunctionParameter;
  }

  public <T extends JSFunctionParameterUpdate> T setJSFunctionParameter(
      JSFunctionParameter jSFunctionParameter) {
    this.jSFunctionParameter = jSFunctionParameter;
    return (T) this;
  }
}
