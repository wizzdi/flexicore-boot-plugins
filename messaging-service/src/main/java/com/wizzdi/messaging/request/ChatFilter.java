package com.wizzdi.messaging.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

public class ChatFilter extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends ChatFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}
}
