package com.flexicore.rules.model;

public enum FunctionTypes {
	EVALUATE("evaluateScript");

	private String functionName;
	FunctionTypes(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionName() {
		return functionName;
	}
}
