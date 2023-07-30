package com.flexicore.organization.app;

import com.flexicore.model.territories.Address;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.request.OrganizationCreate;
import com.flexicore.organization.service.OrganizationService;
import com.flexicore.security.SecurityContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class TestEntities {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    @Lazy
    private SecurityContextBase adminSecurityContext;

    @Bean
    public Organization organization(){
        return organizationService.createOrganization(new OrganizationCreate().setName("test organization"),adminSecurityContext);
    }
}
