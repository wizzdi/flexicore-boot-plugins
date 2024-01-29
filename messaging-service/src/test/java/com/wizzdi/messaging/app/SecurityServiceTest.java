package com.wizzdi.messaging.app;

import com.flexicore.model.Role;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.model.TenantToUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.request.RoleToUserFilter;
import com.wizzdi.flexicore.security.request.TenantToUserFilter;
import com.wizzdi.flexicore.security.service.RoleService;
import com.wizzdi.flexicore.security.service.RoleToUserService;
import com.wizzdi.flexicore.security.service.TenantToUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SecurityServiceTest {

	@Autowired
	private RoleToUserService roleService;
	@Autowired
	private TenantToUserService tenantToUserService;



	public SecurityContextBase getSecurityContext(SecurityUser securityUser){
		Map<String, List<Role>> rols = roleService.listAllRoleToUsers(new RoleToUserFilter().setUsers(Collections.singletonList(securityUser)), null).stream().collect(Collectors.groupingBy(f -> f.getRole().getSecurityTenant().getId(), Collectors.mapping(f -> f.getRole(), Collectors.toList())));
		List<TenantToUser> tenantToUsers = tenantToUserService.listAllTenantToUsers(new TenantToUserFilter().setUsers(Collections.singletonList(securityUser)), null);
		List<SecurityTenant> tenants= tenantToUsers.stream().map(f->f.getTenant()).collect(Collectors.toList());
		SecurityTenant createIn=tenantToUsers.stream().filter(f->f.isDefaultTenant()).findFirst().map(f->f.getTenant()).orElse(null);
		return new SecurityContextBase()
				.setUser(securityUser)
				.setRoleMap(rols)
				.setTenants(tenants)
				.setTenantToCreateIn(createIn);
	}
}
