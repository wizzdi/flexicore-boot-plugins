package com.admin.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.Basic;
import com.flexicore.model.Clazz;
import com.flexicore.model.ClazzLink;
import com.flexicore.model.OperationCategory;
import com.flexicore.model.OperationToClazz;
import com.flexicore.model.PermissionGroup;
import com.flexicore.model.PermissionGroupToBaseclass;
import com.flexicore.model.Role;
import com.flexicore.model.RoleToBaseclass;
import com.flexicore.model.RoleToUser;
import com.flexicore.model.SecuredBasic;
import com.flexicore.model.SecurityEntity;
import com.flexicore.model.SecurityLink;
import com.flexicore.model.SecurityOperation;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.model.SecurityWildcard;
import com.flexicore.model.TenantToBaseClassPremission;
import com.flexicore.model.TenantToUser;
import com.flexicore.model.UserToBaseClass;
import com.flexicore.model.security.SecurityPolicy;
import com.flexicore.model.territories.Address;
import com.flexicore.model.territories.City;
import com.flexicore.model.territories.Country;
import com.flexicore.model.territories.Neighbourhood;
import com.flexicore.model.territories.State;
import com.flexicore.model.territories.Street;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.model.ZipFile;
import com.wizzdi.flexicore.file.model.ZipFileToFileResource;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import java.util.Arrays;
import java.util.HashSet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppTestEntities {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public EntitiesHolder manualEntityHolder() {
    return new EntitiesHolder(
        new HashSet<>(
            Arrays.asList(
                Neighbourhood.class,
                Street.class,
                State.class,
                City.class,
                Address.class,
                MappedPOI.class,
                Country.class,
                Baselink.class,
                SecurityPolicy.class,
                ClazzLink.class,
                RoleToBaseclass.class,
                Clazz.class,
                SecurityEntity.class,
                Baseclass.class,
                PermissionGroup.class,
                UserToBaseClass.class,
                SecurityUser.class,
                Basic.class,
                SecurityLink.class,
                OperationToClazz.class,
                SecurityOperation.class,
                TenantToBaseClassPremission.class,
                SecurityTenant.class,
                PermissionGroupToBaseclass.class,
                RoleToUser.class,
                Role.class,
                SecurityWildcard.class,
                TenantToUser.class,
                OperationCategory.class,
                SecuredBasic.class,
                FileResource.class,
                ZipFile.class,
                ZipFileToFileResource.class,
                MapIcon.class,
                FileResource.class,
                JsonConverter.class,
                SecuredBasic.class)));
  }
}
