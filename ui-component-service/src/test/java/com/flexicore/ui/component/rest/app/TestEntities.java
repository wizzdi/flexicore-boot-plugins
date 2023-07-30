package com.flexicore.ui.component.rest.app;

import com.flexicore.model.SecurityUser;
import com.flexicore.model.TenantToUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.flexicore.security.request.SecurityUserCreate;
import com.wizzdi.flexicore.security.request.TenantToUserCreate;
import com.wizzdi.flexicore.security.service.SecurityUserService;
import com.wizzdi.flexicore.security.service.TenantToUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class TestEntities {

    @Autowired
    private SecurityUserService securityUserService;
    @Autowired
    private SecurityContextBase adminSecurityContext;
    @Autowired
    private TenantToUserService tenantToUserService;

    @Bean
    public SecurityUser user(){
        String name = UUID.randomUUID().toString();
        SecurityUserCreate request = new SecurityUserCreate()
                .setName(name);
      return securityUserService.createSecurityUser(request,adminSecurityContext);
    }

    @Bean
    public TenantToUser tenantToUser(SecurityUser user){
        return tenantToUserService.createTenantToUser(new TenantToUserCreate().setSecurityUser(user).setDefaultTenant(true).setTenant(adminSecurityContext.getTenantToCreateIn()),adminSecurityContext);
    }


}
