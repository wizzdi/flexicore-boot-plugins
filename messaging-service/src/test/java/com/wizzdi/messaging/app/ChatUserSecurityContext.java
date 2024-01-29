package com.wizzdi.messaging.app;

import com.flexicore.model.Role;
import com.flexicore.model.SecurityOperation;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.model.security.SecurityPolicy;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.security.SecurityPermissions;
import com.wizzdi.messaging.model.ChatUser;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class ChatUserSecurityContext extends SecurityContextBase {

	private ChatUser chatUser;
	private final SecurityContextBase securityContextBase;

	public ChatUserSecurityContext(SecurityContextBase securityContextBase) {
		this.securityContextBase = securityContextBase;
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
		return securityContextBase.getTenants();
	}

	@Override
	public SecurityUser getUser() {
		return securityContextBase.getUser();
	}

	@Override
	public SecurityOperation getOperation() {
		return securityContextBase.getOperation();
	}

	@Override
	public Map<String, List<Role>> getRoleMap() {
		return securityContextBase.getRoleMap();
	}

	@Override
	public boolean isImpersonated() {
		return securityContextBase.isImpersonated();
	}

	@Override
	public OffsetDateTime getExpiresDate() {
		return securityContextBase.getExpiresDate();
	}

	@Override
	public SecurityTenant getTenantToCreateIn() {
		return securityContextBase.getTenantToCreateIn();
	}

	@Override
	public List<SecurityPolicy> getSecurityPolicies() {
		return securityContextBase.getSecurityPolicies();
	}

	@Override
	public List<Role> getAllRoles() {
		return securityContextBase.getAllRoles();
	}

	@Override
	public SecurityPermissions getSecurityPermissions() {
		return securityContextBase.getSecurityPermissions();
	}
}
