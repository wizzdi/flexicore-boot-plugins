package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JSFunction;

/** Object Used to Update JSFunction */
public class JSFunctionUpdate extends JSFunctionCreate {

  private String id;

  @JsonIgnore private JSFunction jSFunction;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return JSFunctionUpdate
   */
  public <T extends JSFunctionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return jSFunction */
  @JsonIgnore
  public JSFunction getJSFunction() {
    return this.jSFunction;
  }

  /**
   * @param jSFunction jSFunction to set
   * @return JSFunctionUpdate
   */
  public <T extends JSFunctionUpdate> T setJSFunction(JSFunction jSFunction) {
    this.jSFunction = jSFunction;
    return (T) this;
  }
}
