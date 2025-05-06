package com.flexicore.ui.component.service;


import com.flexicore.model.Clazz;
import com.flexicore.model.PermissionGroup;


import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.component.data.UIComponentRepository;
import com.flexicore.ui.component.model.UIComponent;
import com.flexicore.ui.component.request.UIComponentRegistrationContainer;
import com.flexicore.ui.component.request.UIComponentsRegistrationContainer;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.request.PermissionGroupCreate;
import com.wizzdi.flexicore.security.request.PermissionGroupFilter;
import com.wizzdi.flexicore.security.request.PermissionGroupToBaseclassCreate;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.PermissionGroupService;
import com.wizzdi.flexicore.security.service.PermissionGroupToBaseclassService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Asaf on 12/07/2017.
 */

@Extension
@Component
public class UIComponentService implements Plugin {

    @Autowired
    
    private UIComponentRepository uiComponentRepository;

    @Autowired
    @Lazy
    private SecurityContext adminSecurityContext;
    @Autowired
    private PermissionGroupService permissionGroupService;
    @Autowired
    private PermissionGroupToBaseclassService permissionGroupToBaseclassService;

   private static final Logger logger = LoggerFactory.getLogger(UIComponentService.class);






    public List<UIComponent> registerAndGetAllowedUIComponents(List<UIComponentRegistrationContainer> componentsToRegister, SecurityContext securityContext) {
        Map<String,UIComponentRegistrationContainer> externalIds=componentsToRegister.stream().collect(Collectors.toMap(f->f.getExternalId(),f->f,(a,b)->a));
        List<UIComponent> existing=new ArrayList<>();
        for (List<String> externalIdsBatch : partition(new ArrayList<>(externalIds.keySet()), 50)) {
            existing.addAll(uiComponentRepository.getExistingUIComponentsByIds(new HashSet<>(externalIdsBatch)));
        }
        Set<String> groupExternalIds=componentsToRegister.stream().filter(f->f.getGroups()!=null).map(f->f.getGroups().split(",")).flatMap(Stream::of).collect(Collectors.toSet());
        Map<String, PermissionGroup> permissionGroupMap=groupExternalIds.isEmpty()?new HashMap<>():permissionGroupService.listAllPermissionGroups(new PermissionGroupFilter().setExternalIds(groupExternalIds),null).stream().collect(Collectors.toMap(f->f.getExternalId(), f->f,(a, b)->a));
        Map<String,UIComponent> existingMap=existing.stream().collect(Collectors.toMap(f->f.getExternalId(),f->f,(a,b)->a));

        List<UIComponent> accessible=new ArrayList<>();
        for (List<String> idsBatch : partition(existing.stream().map(f->f.getId()).collect(Collectors.toList()),50)) {
            accessible.addAll(uiComponentRepository.listByIds(UIComponent.class,new HashSet<>(idsBatch), securityContext));
        }

        List<UIComponentRegistrationContainer> componentsToCreate=externalIds.values().parallelStream().collect(Collectors.toList());
        List<Object> toMerge=new ArrayList<>();
        boolean isAdmin=securityContext.getUser().getId().equals(adminSecurityContext.getUser().getId());
        Map<String,List<UIComponent>> permissionGroupExternalIdToUIComponent=new HashMap<>();
        for (UIComponentRegistrationContainer uiComponentRegistrationContainer : componentsToCreate) {
            UIComponent uiComponent= existingMap.get(uiComponentRegistrationContainer.getExternalId());

            if(uiComponent==null){
                uiComponent= createUIComponentNoMerge(uiComponentRegistrationContainer,adminSecurityContext);
                toMerge.add(uiComponent);
                existingMap.put(uiComponent.getExternalId(),uiComponent);
                if(isAdmin){
                    accessible.add(uiComponent);
                }
            }
            else{
                if(uiComponent.isSoftDelete()){
                    uiComponent.setSoftDelete(false);
                    toMerge.add(uiComponent);
                }
            }
            Set<String> permissionGroupsExternalIds=uiComponentRegistrationContainer.getGroups()==null?new HashSet<>():Stream.of(uiComponentRegistrationContainer.getGroups().split(",")).collect(Collectors.toSet());
            for (String permissionGroupsExternalId : permissionGroupsExternalIds) {
                PermissionGroupCreate createPermissionGroupRequest=new PermissionGroupCreate()
                        .setExternalId(permissionGroupsExternalId)
                        .setName(permissionGroupsExternalId)
                        .setDescription(permissionGroupsExternalId);
                PermissionGroup permissionGroup=permissionGroupMap.get(permissionGroupsExternalId);
                if(permissionGroup==null){
                    permissionGroup=permissionGroupService.createPermissionGroupNoMerge(createPermissionGroupRequest,adminSecurityContext);
                    permissionGroupMap.put(permissionGroupsExternalId,permissionGroup);
                    toMerge.add(permissionGroup);
                }
                else{
                    if(permissionGroupService.updatePermissionGroupNoMerge(createPermissionGroupRequest, permissionGroup)){
                        toMerge.add(permissionGroup);
                    }
                }
                permissionGroupExternalIdToUIComponent.computeIfAbsent(permissionGroupsExternalId,f->new ArrayList<>()).add(uiComponent);
            }



        }


        uiComponentRepository.massMerge(toMerge);
        for (Map.Entry<String, List<UIComponent>> permissionGroupToUIComponent : permissionGroupExternalIdToUIComponent.entrySet()) {
            PermissionGroup permissionGroup=permissionGroupMap.get(permissionGroupToUIComponent.getKey());
            List<UIComponent> uiComponents=permissionGroupToUIComponent.getValue();
            for (UIComponent uiComponent : uiComponents) {
                permissionGroupToBaseclassService.createPermissionGroupToBaseclass(new PermissionGroupToBaseclassCreate().setPermissionGroup(permissionGroup).setSecuredId(uiComponent.getSecurityId()).setSecuredType(Clazz.ofClass(uiComponent.getClass())),adminSecurityContext);

            }

        }
        return accessible;


    }

    private UIComponent createUIComponentNoMerge(UIComponentRegistrationContainer uiComponentRegistrationContainer, SecurityContext securityContext) {
        UIComponent uiComponent=new UIComponent();
        uiComponent.setId(UUID.randomUUID().toString());
        uiComponent.setExternalId(uiComponentRegistrationContainer.getExternalId());
        uiComponent.setName(uiComponentRegistrationContainer.getName());
        uiComponent.setDescription(uiComponentRegistrationContainer.getDescription());
        BaseclassService.createSecurityObjectNoMerge(uiComponent,securityContext);
        return uiComponent;

    }




    public void validate(UIComponentsRegistrationContainer uiComponentsRegistrationContainer) {

        for (UIComponentRegistrationContainer uiComponentRegistrationContainer : uiComponentsRegistrationContainer.getComponentsToRegister()) {
            if(uiComponentRegistrationContainer.getExternalId()==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"uiComponentRegistrationContainer.externalId is mandatory");
            }
        }
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        return new ArrayList<>(IntStream.iterate(0, i -> i + size)
                .limit((long) Math.ceil((double) list.size() / size))
                .mapToObj(cur -> list.subList(cur, Math.min(cur + size, list.size())))
                .collect(Collectors.toList()));
    }
}
