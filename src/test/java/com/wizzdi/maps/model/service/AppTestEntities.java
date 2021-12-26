package com.wizzdi.maps.model.service;

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
import com.wizzdi.maps.model.MappedPOI;
import java.util.Arrays;
import java.util.HashSet;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import parking.gear.model.Building;
import parking.gear.model.BuildingFloor;
import parking.gear.model.Camera;
import parking.gear.model.Gate;
import parking.gear.model.LPREngine;
import parking.gear.model.Lane;
import parking.gear.model.ParkingArea;
import parking.gear.model.ParkingLot;
import parking.gear.model.ParkingSpace;
import parking.gear.model.ParkingSpaceState;
import parking.gear.model.ParkingSpaceToParkingSpaceState;

@Configuration
public class AppTestEntities {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public EntitiesHolder manualEntityHolder() {
    return new EntitiesHolder(
        new HashSet<>(
            Arrays.asList(
                Building.class,
                Neighbourhood.class,
                Street.class,
                State.class,
                City.class,
                Address.class,
                ParkingSpace.class,
                ParkingArea.class,
                ParkingLot.class,
                MappedPOI.class,
                Lane.class,
                Camera.class,
                Country.class,
                BuildingFloor.class,
                Gate.class,
                ParkingSpaceToParkingSpaceState.class,
                ParkingSpaceState.class,
                Baseclass.class,
                LPREngine.class,
                OperationToClazz.class,
                SecurityTenant.class,
                PermissionGroup.class,
                UserToBaseClass.class,
                FileResource.class,
                ZipFile.class,
                SecurityWildcard.class,
                PermissionGroupToBaseclass.class,
                SecurityOperation.class,
                Baselink.class,
                OperationCategory.class,
                TenantToUser.class,
                RoleToBaseclass.class,
                SecurityUser.class,
                RoleToUser.class,
                Clazz.class,
                Role.class,
                SecurityEntity.class,
                ZipFileToFileResource.class,
                SecurityLink.class,
                TenantToBaseClassPremission.class,
                SecurityPolicy.class,
                ClazzLink.class,
                SecuredBasic.class,
                Basic.class,
                FileResource.class,
                JsonConverter.class,
                SecuredBasic.class)));
  }
}
