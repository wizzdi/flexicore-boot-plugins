package com.wizzdi.messaging.app;

import com.flexicore.model.Role;
import com.flexicore.model.SecurityOperation;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.model.security.SecurityPolicy;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.messaging.model.ChatUser;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class ChatUserSecurityContext<ST extends SecurityTenant,O extends SecurityOperation,R extends Role> extends SecurityContextBase<ST, SecurityUser,O,R> {

	private ChatUser chatUser;
	private SecurityContextBase<ST,SecurityUser,O,R> securityContextBase;

	public ChatUserSecurityContext(SecurityContextBase<ST, SecurityUser, O, R> securityContextBase) {
		this.securityContextBase = securityContextBase;
	}

	public ChatUser getChatUser() {
		return chatUser;
	}

	public <T extends ChatUserSecurityContext<ST, O, R>> T setChatUser(ChatUser chatUser) {
		this.chatUser = chatUser;
		return (T) this;
	}

	@Override
	public List<ST> getTenants() {
		return securityContextBase.getTenants();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setTenants(List<ST> tenants) {
		return securityContextBase.setTenants(tenants);
	}

	@Override
	public SecurityUser getUser() {
		return securityContextBase.getUser();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setUser(SecurityUser user) {
		return securityContextBase.setUser(user);
	}

	@Override
	public O getOperation() {
		return securityContextBase.getOperation();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setOperation(O operation) {
		return securityContextBase.setOperation(operation);
	}

	@Override
	public Map<String, List<R>> getRoleMap() {
		return securityContextBase.getRoleMap();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setRoleMap(Map<String, List<R>> roleMap) {
		return securityContextBase.setRoleMap(roleMap);
	}

	@Override
	public boolean isImpersonated() {
		return securityContextBase.isImpersonated();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setImpersonated(boolean impersonated) {
		return securityContextBase.setImpersonated(impersonated);
	}

	@Override
	public OffsetDateTime getExpiresDate() {
		return securityContextBase.getExpiresDate();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setExpiresDate(OffsetDateTime expiresDate) {
		return securityContextBase.setExpiresDate(expiresDate);
	}

	@Override
	public ST getTenantToCreateIn() {
		return securityContextBase.getTenantToCreateIn();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setTenantToCreateIn(ST tenantToCreateIn) {
		return securityContextBase.setTenantToCreateIn(tenantToCreateIn);
	}

	@Override
	public List<SecurityPolicy> getSecurityPolicies() {
		return securityContextBase.getSecurityPolicies();
	}

	@Override
	public <T extends SecurityContextBase<ST, SecurityUser, O, R>> T setSecurityPolicies(List<SecurityPolicy> securityPolicies) {
		return securityContextBase.setSecurityPolicies(securityPolicies);
	}
}
