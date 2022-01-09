package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class JSFunctionParameter extends SecuredBasic {

  private Integer ordinal;

  private String parameterType;

  @ManyToOne(targetEntity = JsFunction.class)
  private JsFunction jsFunction;

  /** @return ordinal */
  public Integer getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return JSFunctionParameter
   */
  public <T extends JSFunctionParameter> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return parameterType */
  public String getParameterType() {
    return this.parameterType;
  }

  /**
   * @param parameterType parameterType to set
   * @return JSFunctionParameter
   */
  public <T extends JSFunctionParameter> T setParameterType(String parameterType) {
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
   * @return JSFunctionParameter
   */
  public <T extends JSFunctionParameter> T setJsFunction(JsFunction jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }
}
