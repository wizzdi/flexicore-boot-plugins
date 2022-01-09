package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.file.model.FileResource;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class JsFunction extends SecuredBasic {

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource evaluatingJSCode;

  private String returnType;

  private String methodName;

  /** @return evaluatingJSCode */
  @ManyToOne(targetEntity = FileResource.class)
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return JsFunction
   */
  public <T extends JsFunction> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  /** @return returnType */
  public String getReturnType() {
    return this.returnType;
  }

  /**
   * @param returnType returnType to set
   * @return JsFunction
   */
  public <T extends JsFunction> T setReturnType(String returnType) {
    this.returnType = returnType;
    return (T) this;
  }

  /** @return methodName */
  public String getMethodName() {
    return this.methodName;
  }

  /**
   * @param methodName methodName to set
   * @return JsFunction
   */
  public <T extends JsFunction> T setMethodName(String methodName) {
    this.methodName = methodName;
    return (T) this;
  }
}
