package com.flexicore.rules;

import com.flexicore.model.Baseclass;

import com.flexicore.model.Basic;
import com.flexicore.model.Clazz;
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

import com.flexicore.model.TenantToUser;

import com.flexicore.model.security.SecurityPolicy;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.model.JSFunctionParameter;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioSavableEvent;
import com.flexicore.rules.model.ScenarioToAction;
import com.flexicore.rules.model.ScenarioToDataSource;
import com.flexicore.rules.model.ScenarioToTrigger;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.model.ZipFile;
import com.wizzdi.flexicore.file.model.ZipFileToFileResource;
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
                    DynamicExecution.class,
                PermissionGroup.class,
                
                SecurityEntity.class,
                Baseclass.class,
                SecurityTenant.class,
                Clazz.class,
                SecurityWildcard.class,
                Basic.class,
                SecurityWildcard.class,
                SecurityEntity.class,
                SecuredBasic.class,
               
                RoleToUser.class,
                SecurityOperation.class,
                OperationCategory.class,
                RoleToBaseclass.class,
                
                SecurityUser.class,
                Role.class,
                SecurityPolicy.class,
                TenantToUser.class,
                PermissionGroupToBaseclass.class,
                OperationToClazz.class,
                SecurityLink.class,
                Role.class,
                Clazz.class,
                Basic.class,
                SecurityUser.class,
                SecurityPolicy.class,
                PermissionGroupToBaseclass.class,
                PermissionGroup.class,
                RoleToUser.class,
                TenantToUser.class,
                RoleToBaseclass.class,
                
                Baseclass.class,
                
                SecuredBasic.class,
                OperationToClazz.class,
                SecurityTenant.class,
                FileResource.class,
                ZipFile.class,
                ZipFileToFileResource.class,
                ScenarioSavableEvent.class,
                Scenario.class,
                JSFunctionParameter.class,
                JSFunction.class,
                ScenarioTrigger.class,
                ScenarioAction.class,
                DataSource.class,
                ScenarioTriggerType.class,
                ScenarioToAction.class,
                ScenarioToTrigger.class,
                ScenarioToDataSource.class,
                FileResource.class,
                JsonConverter.class,
                SecuredBasic.class)));
  }
}
