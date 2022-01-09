package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JSFunction;

public class JSFunctionUpdate extends JSFunctionCreate {

  private String id;
  @JsonIgnore private JSFunction jSFunction;

  public String getId() {
    return id;
  }

  public <T extends JSFunctionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public JSFunction getJSFunction() {
    return jSFunction;
  }

  public <T extends JSFunctionUpdate> T setJSFunction(JSFunction jSFunction) {
    this.jSFunction = jSFunction;
    return (T) this;
  }
}
