package com.wizzdi.messaging.app;

import com.flexicore.model.Role;
import com.flexicore.model.SecurityOperation;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.model.security.SecurityPolicy;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.messaging.model.ChatUser;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class ChatUserSecurityContext extends SecurityContext {

	private ChatUser chatUser;
	private final SecurityContext SecurityContext;

	public ChatUserSecurityContext(SecurityContext SecurityContext) {
		this.SecurityContext = SecurityContext;
	}

	public ChatUser getChatUser() {
		return chatUser;
	}

	public <T extends ChatUserSecurityContext> T setChatUser(ChatUser chatUser) {
		this.chatUser = chatUser;
		return (T) this;
	}

	@Override
	public List<SecurityTenant> getTenants() {
		return SecurityContext.getTenants();
	}

	@Override
	public SecurityUser getUser() {
		return SecurityContext.getUser();
	}

	@Override
	public SecurityOperation getOperation() {
		return SecurityContext.getOperation();
	}

	@Override
	public Map<String, List<Role>> getRoleMap() {
		return SecurityContext.getRoleMap();
	}

	@Override
	public boolean isImpersonated() {
		return SecurityContext.isImpersonated();
	}

	@Override
	public OffsetDateTime getExpiresDate() {
		return SecurityContext.getExpiresDate();
	}

	@Override
	public SecurityTenant getTenantToCreateIn() {
		return SecurityContext.getTenantToCreateIn();
	}

	@Override
	public List<SecurityPolicy> getSecurityPolicies() {
		return SecurityContext.getSecurityPolicies();
	}

	@Override
	public List<Role> getAllRoles() {
		return SecurityContext.getAllRoles();
	}


}
