package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JsFunction;

public class JsFunctionUpdate extends JsFunctionCreate {

  private String id;
  @JsonIgnore private JsFunction jsFunction;

  public String getId() {
    return id;
  }

  public <T extends JsFunctionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public JsFunction getJsFunction() {
    return jsFunction;
  }

  public <T extends JsFunctionUpdate> T setJsFunction(JsFunction jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }
}
