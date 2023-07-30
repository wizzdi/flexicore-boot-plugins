package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class JSFunctionParameter extends SecuredBasic {

  private Integer ordinal;

  private String parameterType;

  @ManyToOne(targetEntity = JSFunction.class)
  private JSFunction jsFunction;

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
  @ManyToOne(targetEntity = JSFunction.class)
  public JSFunction getJsFunction() {
    return this.jsFunction;
  }

  /**
   * @param jsFunction jsFunction to set
   * @return JSFunctionParameter
   */
  public <T extends JSFunctionParameter> T setJsFunction(JSFunction jsFunction) {
    this.jsFunction = jsFunction;
    return (T) this;
  }
}
