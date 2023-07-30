package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create JSFunction */
public class JSFunctionCreate extends BasicCreate {

  @JsonIgnore private FileResource evaluatingJSCode;

  private String returnType;

  private String evaluatingJSCodeId;

  private String methodName;

  /** @return evaluatingJSCode */
  @JsonIgnore
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return JSFunctionCreate
   */
  public <T extends JSFunctionCreate> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  /** @return returnType */
  public String getReturnType() {
    return this.returnType;
  }

  /**
   * @param returnType returnType to set
   * @return JSFunctionCreate
   */
  public <T extends JSFunctionCreate> T setReturnType(String returnType) {
    this.returnType = returnType;
    return (T) this;
  }

  /** @return evaluatingJSCodeId */
  public String getEvaluatingJSCodeId() {
    return this.evaluatingJSCodeId;
  }

  /**
   * @param evaluatingJSCodeId evaluatingJSCodeId to set
   * @return JSFunctionCreate
   */
  public <T extends JSFunctionCreate> T setEvaluatingJSCodeId(String evaluatingJSCodeId) {
    this.evaluatingJSCodeId = evaluatingJSCodeId;
    return (T) this;
  }

  /** @return methodName */
  public String getMethodName() {
    return this.methodName;
  }

  /**
   * @param methodName methodName to set
   * @return JSFunctionCreate
   */
  public <T extends JSFunctionCreate> T setMethodName(String methodName) {
    this.methodName = methodName;
    return (T) this;
  }
}
