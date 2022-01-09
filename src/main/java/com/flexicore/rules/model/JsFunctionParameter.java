package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class JsFunctionParameter extends SecuredBasic {

  private String ordinal;

  private String parameterType;

  @ManyToOne(targetEntity = JsFunction.class)
  private JsFunction jsFunction;

  /** @return ordinal */
  public String getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return JsFunctionParameter
   */
  public <T extends JsFunctionParameter> T setOrdinal(String ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return parameterType */
  public String getParameterType() {
    return this.parameterType;
  }

  /**
   * @param parameterType parameterType to set
   * @return JsFunctionParameter
   */
  public <T extends JsFunctionParameter> T setParameterType(String parameterType) {
    this.parameterType = parameterType;
    return (T) this;
  }

  /** @return jsFunction */
  @ManyToOne(targetEntity = JsFunction.class)
  public JsFunction getJsFunction() {
    return this.jsFunction;
  }

  /**
   * @param jsFunction jsFunction to set
   * @return JsFunctionParameter
   */
  public <T extends JsFunctionParameter> T setJsFunction(JsFunction jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }
}
