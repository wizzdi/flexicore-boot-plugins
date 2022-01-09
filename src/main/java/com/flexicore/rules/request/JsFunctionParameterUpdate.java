package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.JsFunctionParameter;

public class JsFunctionParameterUpdate extends JsFunctionParameterCreate {

  private String id;
  @JsonIgnore private JsFunctionParameter jsFunctionParameter;

  public String getId() {
    return id;
  }

  public <T extends JsFunctionParameterUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public JsFunctionParameter getJsFunctionParameter() {
    return jsFunctionParameter;
  }

  public <T extends JsFunctionParameterUpdate> T setJsFunctionParameter(
      JsFunctionParameter jsFunctionParameter) {
    this.jsFunctionParameter = jsFunctionParameter;
    return (T) this;
  }
}
