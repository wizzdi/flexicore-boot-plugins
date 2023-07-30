package com.flexicore.license.response;

public class ClazzCount {

	private String tenantId;
	private String clazzName;
	private Long count;

	public ClazzCount(String clazzName, Long count) {
		this(null, clazzName,count);
	}

	public ClazzCount(String tenantId, String clazzName, Long count) {
		this.tenantId = tenantId;
		this.clazzName = clazzName;
		this.count = count;
	}


	public String getTenantId() {
		return tenantId;
	}

	public String getClazzName() {
		return clazzName;
	}

	public Long getCount() {
		return count;
	}
}
