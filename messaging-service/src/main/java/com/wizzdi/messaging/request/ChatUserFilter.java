package com.wizzdi.messaging.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

public class ChatUserFilter extends PaginationFilter {

	private String userNameLike;
	private BasicPropertiesFilter basicPropertiesFilter;


	public String getUserNameLike() {
		return userNameLike;
	}

	public <T extends ChatUserFilter> T setUserNameLike(String userNameLike) {
		this.userNameLike = userNameLike;
		return (T) this;
	}

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends ChatUserFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}
}
