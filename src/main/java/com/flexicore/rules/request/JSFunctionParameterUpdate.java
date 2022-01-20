package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JSFunctionParameter;

/** Object Used to Update JSFunctionParameter */
public class JSFunctionParameterUpdate extends JSFunctionParameterCreate {

  private String id;

  @JsonIgnore private JSFunctionParameter jSFunctionParameter;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return JSFunctionParameterUpdate
   */
  public <T extends JSFunctionParameterUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return jSFunctionParameter */
  @JsonIgnore
  public JSFunctionParameter getJSFunctionParameter() {
    return this.jSFunctionParameter;
  }

  /**
   * @param jSFunctionParameter jSFunctionParameter to set
   * @return JSFunctionParameterUpdate
   */
  public <T extends JSFunctionParameterUpdate> T setJSFunctionParameter(
      JSFunctionParameter jSFunctionParameter) {
    this.jSFunctionParameter = jSFunctionParameter;
    return (T) this;
  }
}
