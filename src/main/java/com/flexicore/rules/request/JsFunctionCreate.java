package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class JsFunctionCreate extends BasicCreate {

  private String evaluatingJSCodeId;

  @JsonIgnore private FileResource evaluatingJSCode;

  private String returnType;

  private String methodName;

  public String getEvaluatingJSCodeId() {
    return this.evaluatingJSCodeId;
  }

  public <T extends JsFunctionCreate> T setEvaluatingJSCodeId(String evaluatingJSCodeId) {
    this.evaluatingJSCodeId = evaluatingJSCodeId;
    return (T) this;
  }

  @JsonIgnore
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  public <T extends JsFunctionCreate> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  public String getReturnType() {
    return this.returnType;
  }

  public <T extends JsFunctionCreate> T setReturnType(String returnType) {
    this.returnType = returnType;
    return (T) this;
  }

  public String getMethodName() {
    return this.methodName;
  }

  public <T extends JsFunctionCreate> T setMethodName(String methodName) {
    this.methodName = methodName;
    return (T) this;
  }
}
