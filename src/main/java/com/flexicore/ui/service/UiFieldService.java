package com.flexicore.ui.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.*;
import com.flexicore.request.CategoryCreate;
import com.flexicore.request.CategoryFilter;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaselinkService;
import com.flexicore.service.CategoryService;
import com.flexicore.ui.data.UiFieldRepository;
import com.flexicore.ui.model.*;
import com.flexicore.ui.request.*;
import com.flexicore.ui.response.PresetToRoleContainer;
import com.flexicore.ui.response.PresetToTenantContainer;
import com.flexicore.ui.response.PresetToUserContainer;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class UiFieldService implements ServicePlugin {

    @Autowired
    private Logger logger;

    @PluginInfo(version = 1)
    @Autowired
    private UiFieldRepository uiFieldRepository;

    @PluginInfo(version = 1)
    @Autowired
    private PresetToEntityService presetToEntityService;

    @Autowired
    private BaselinkService baselinkService;

    @PluginInfo(version = 1)
    @Autowired
    private PresetService presetService;

    @Autowired
    private CategoryService categoryService;

    public UiField updateUiField(UiFieldUpdate updateUiField,
                                 SecurityContext securityContext) {
        if (updateUiFieldNoMerge(updateUiField, updateUiField.getUiField())) {
            uiFieldRepository.merge(updateUiField.getUiField());
        }
        return updateUiField.getUiField();
    }

    public boolean updateUiFieldNoMerge(UiFieldCreate updateUiField,
                                        UiField uiField) {
        boolean update = false;
        if (updateUiField.getVisible() != null
                && updateUiField.getVisible() != uiField.isVisible()) {
            update = true;
            uiField.setVisible(updateUiField.getVisible());
        }

        if (updateUiField.getPriority() != null
                && updateUiField.getPriority() != uiField.getPriority()) {
            update = true;
            uiField.setPriority(updateUiField.getPriority());
        }

        if (updateUiField.getDescription() != null
                && (uiField.getDescription() == null || !updateUiField
                .getDescription().equals(uiField.getDescription()))) {
            update = true;
            uiField.setDescription(updateUiField.getDescription());
        }

        if (updateUiField.getName() != null
                && !updateUiField.getName().equals(uiField.getName())) {
            update = true;
            uiField.setName(updateUiField.getName());
        }

        if (updateUiField.getCategory() != null
                && (uiField.getCategory() == null || !updateUiField
                .getCategory().getId()
                .equals(uiField.getCategory().getId()))) {
            update = true;
            uiField.setCategory(updateUiField.getCategory());
        }

        if (updateUiField.getPreset() != null
                && (uiField.getPreset() == null || !updateUiField.getPreset()
                .getId().equals(uiField.getPreset().getId()))) {
            update = true;
            uiField.setPreset(updateUiField.getPreset());
        }

        if (updateUiField.getDisplayName() != null
                && !updateUiField.getDisplayName().equals(
                uiField.getDisplayName())) {
            update = true;
            uiField.setDisplayName(updateUiField.getDisplayName());
        }

        return update;
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
                                                 List<String> batchString, SecurityContext securityContext) {
        return uiFieldRepository.getByIdOrNull(id, c, batchString,
                securityContext);
    }

    public List<UiField> listAllUiFields(UiFieldFiltering uiFieldFiltering,
                                         SecurityContext securityContext) {
        return uiFieldRepository.listAllUiFields(uiFieldFiltering,
                securityContext);
    }

    public UiField createUiField(UiFieldCreate createUiField,
                                 SecurityContext securityContext) {
        UiField uiField = createUiFieldNoMerge(createUiField, securityContext);
        uiFieldRepository.merge(uiField);
        return uiField;

    }

    public UiField createUiFieldNoMerge(UiFieldCreate createUiField,
                                        SecurityContext securityContext) {
        UiField uiField = new UiField(createUiField.getName(),
                securityContext);
        updateUiFieldNoMerge(createUiField, uiField);
        return uiField;
    }

    public void validate(PresetToUserCreate linkPresetToUser,
                         SecurityContext securityContext) {
        presetToEntityService.validate(linkPresetToUser, securityContext);
        String userId = linkPresetToUser.getUserId();
        User user = userId != null ? getByIdOrNull(userId, User.class, null,
                securityContext) : null;
        if (user == null) {
            throw new BadRequestException("no user with id " + userId);
        }
        linkPresetToUser.setUser(user);
    }

    public void validate(PresetToRoleCreate presetToRoleCreate,
                         SecurityContext securityContext) {
        presetToEntityService.validate(presetToRoleCreate, securityContext);

        String roleId = presetToRoleCreate.getRoleId();
        Role role = roleId != null ? getByIdOrNull(roleId, Role.class, null,
                securityContext) : null;
        if (role == null) {
            throw new BadRequestException("no role with id " + roleId);
        }
        presetToRoleCreate.setRole(role);
    }

    public void validate(PresetToTenantCreate presetToTenantCreate,
                         SecurityContext securityContext) {
        presetToEntityService.validate(presetToTenantCreate, securityContext);

        String tenantId = presetToTenantCreate.getTenantId();
        Tenant tenant = tenantId != null ? getByIdOrNull(tenantId,
                Tenant.class, null, securityContext) : null;
        if (tenant == null) {
            throw new BadRequestException("no tenant with id " + tenantId);
        }
        presetToTenantCreate.setTenant(tenant);
    }

    public PresetToRole createPresetToRole(
            PresetToRoleCreate presetToRoleCreate,
            SecurityContext securityContext) {
        PresetToRole toRet;
        List<PresetToRole> existing = listAllPresetToRole(
                new PresetToRoleFilter()
                        .setRoles(
                                Collections.singletonList(presetToRoleCreate
                                        .getRole())).setPresets(
                        Collections.singletonList(presetToRoleCreate
                                .getPreset())), securityContext);
        if (existing.isEmpty()) {
            toRet = createPresetToRoleNoMerge(presetToRoleCreate,
                    securityContext);
            uiFieldRepository.merge(toRet);
        } else {
            toRet = existing.get(0);
        }

        return toRet;
    }

    private PresetToRole createPresetToRoleNoMerge(
            PresetToRoleCreate presetToRoleCreate,
            SecurityContext securityContext) {
        PresetToRole presetToRole = new PresetToRole(
                "presetToRole", securityContext);
        presetToRole.setLeftside(presetToRoleCreate.getPreset());
        presetToRole.setRightside(presetToRoleCreate.getRole());
        updatePresetToRoleNoMerge(presetToRoleCreate, presetToRole);
        return presetToRole;
    }

    public PresetToTenant createPresetToTenant(
            PresetToTenantCreate presetToTenantCreate,
            SecurityContext securityContext) {
        PresetToTenant toRet;
        List<PresetToTenant> existing = listAllPresetToTenant(
                new PresetToTenantFilter().setTenants(
                        Collections.singletonList(presetToTenantCreate
                                .getTenant())).setPresets(
                        Collections.singletonList(presetToTenantCreate
                                .getPreset())), securityContext);
        if (existing.isEmpty()) {
            toRet = createPresetToTenantNoMerge(presetToTenantCreate,
                    securityContext);
            uiFieldRepository.merge(toRet);
        } else {
            toRet = existing.get(0);
        }

        return toRet;
    }

    private PresetToTenant createPresetToTenantNoMerge(
            PresetToTenantCreate presetToTenantCreate,
            SecurityContext securityContext) {
        PresetToTenant presetToTenant = new PresetToTenant(
                "presetToTenant", securityContext);
        presetToTenant.setLeftside(presetToTenantCreate.getPreset());
        presetToTenant.setRightside(presetToTenantCreate.getTenant());
        updatePresetToTenantNoMerge(presetToTenantCreate, presetToTenant);
        return presetToTenant;
    }

    public PresetToUser createPresetToUser(
            PresetToUserCreate presetToUserCreate,
            SecurityContext securityContext) {
        PresetToUser toRet;
        List<PresetToUser> existing = listAllPresetToUser(
                new PresetToUserFilter()
                        .setUsers(
                                Collections.singletonList(presetToUserCreate
                                        .getUser())).setPresets(
                        Collections.singletonList(presetToUserCreate
                                .getPreset())), securityContext);
        if (existing.isEmpty()) {
            toRet = createPresetToUserNoMerge(presetToUserCreate,
                    securityContext);
            uiFieldRepository.merge(toRet);
        } else {
            toRet = existing.get(0);
        }

        return toRet;
    }

    private PresetToUser createPresetToUserNoMerge(
            PresetToUserCreate presetToUserCreate,
            SecurityContext securityContext) {
        PresetToUser presetToUser = new PresetToUser("presetToUser", securityContext);
        presetToUser.setLeftside(presetToUserCreate.getPreset());
        presetToUser.setRightside(presetToUserCreate.getUser());
        updatePresetToUserNoMerge(presetToUserCreate, presetToUser);
        return presetToUser;
    }

    public boolean updatePresetToTenantNoMerge(
            PresetToTenantCreate presetToTenantCreate,
            PresetToTenant presetToTenant) {
        boolean update = presetToEntityService.presetToEntityUpdateNoMerge(
                presetToTenantCreate, presetToTenant);

        if (presetToTenantCreate.getTenant() != null
                && (presetToTenant.getRightside() == null || !presetToTenantCreate
                .getTenant().getId()
                .equals(presetToTenant.getRightside().getId()))) {
            presetToTenant.setRightside(presetToTenantCreate.getTenant());
            update = true;
        }
        return update;
    }

    public PresetToTenant updatePresetToTenant(
            PresetToTenantUpdate updateLinkPresetToTenant,
            SecurityContext securityContext) {
        if (updatePresetToTenantNoMerge(updateLinkPresetToTenant,
                updateLinkPresetToTenant.getPresetToTenant())) {
            uiFieldRepository.merge(updateLinkPresetToTenant
                    .getPresetToTenant());
        }
        return updateLinkPresetToTenant.getPresetToTenant();
    }

    public PresetToUser updatePresetToUser(
            PresetToUserUpdate updateLinkPresetToUser,
            SecurityContext securityContext) {
        if (updatePresetToUserNoMerge(updateLinkPresetToUser,
                updateLinkPresetToUser.getPresetToUser())) {
            uiFieldRepository.merge(updateLinkPresetToUser.getPresetToUser());
        }
        return updateLinkPresetToUser.getPresetToUser();
    }

    public boolean updatePresetToUserNoMerge(
            PresetToUserCreate presetToUserCreate, PresetToUser presetToUser) {
        boolean update = presetToEntityService.presetToEntityUpdateNoMerge(
                presetToUserCreate, presetToUser);
        if (presetToUserCreate.getUser() != null
                && (presetToUser.getRightside() == null || !presetToUserCreate
                .getUser().getId()
                .equals(presetToUser.getRightside().getId()))) {
            presetToUser.setRightside(presetToUserCreate.getUser());
            update = true;
        }
        return update;
    }

    public PresetToRole updatePresetToRole(
            PresetToRoleUpdate updateLinkPresetToRole,
            SecurityContext securityContext) {
        if (updatePresetToRoleNoMerge(updateLinkPresetToRole,
                updateLinkPresetToRole.getPresetToRole())) {
            uiFieldRepository.merge(updateLinkPresetToRole.getPresetToRole());
        }
        return updateLinkPresetToRole.getPresetToRole();
    }

    public boolean updatePresetToRoleNoMerge(
            PresetToRoleCreate presetToRoleCreate, PresetToRole presetToRole) {
        boolean update = presetToEntityService.presetToEntityUpdateNoMerge(
                presetToRoleCreate, presetToRole);
        if (presetToRoleCreate.getRole() != null
                && (presetToRole.getRightside() == null || !presetToRoleCreate
                .getRole().getId()
                .equals(presetToRole.getRightside().getId()))) {
            presetToRole.setRightside(presetToRoleCreate.getRole());
            update = true;
        }
        return update;
    }

    public void validate(PresetToRoleFilter presetToRoleFilter,
                         SecurityContext securityContext) {
        Set<String> roleIds = presetToRoleFilter.getRoleIds();
        Map<String, Role> map = roleIds.isEmpty()
                ? new HashMap<>()
                : uiFieldRepository
                .listByIds(Role.class, roleIds, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        roleIds.removeAll(map.keySet());
        if (!roleIds.isEmpty()) {
            throw new BadRequestException("No Roles with ids " + roleIds);
        }
        presetToRoleFilter.setRoles(new ArrayList<>(map.values()));

    }

    public PaginationResponse<PresetToRoleContainer> getAllPresetToRole(
            PresetToRoleFilter presetToRoleFilter,
            SecurityContext securityContext) {
        List<PresetToRoleContainer> list = listAllPresetToRole(
                presetToRoleFilter, securityContext).parallelStream()
                .map(f -> new PresetToRoleContainer(f))
                .collect(Collectors.toList());
        long count = uiFieldRepository.countAllPresetToRoles(
                presetToRoleFilter, securityContext);
        return new PaginationResponse<>(list, presetToRoleFilter, count);
    }

    private List<PresetToRole> listAllPresetToRole(
            PresetToRoleFilter presetToRoleFilter,
            SecurityContext securityContext) {
        return uiFieldRepository.listAllPresetToRoles(presetToRoleFilter,
                securityContext);
    }

    public void validate(PresetToTenantFilter presetToTenantFilter,
                         SecurityContext securityContext) {
        Set<String> tenantIds = presetToTenantFilter.getTenantIdsForPreset();
        Map<String, Tenant> map = tenantIds.isEmpty()
                ? new HashMap<>()
                : uiFieldRepository
                .listByIds(Tenant.class, tenantIds, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        tenantIds.removeAll(map.keySet());
        if (!tenantIds.isEmpty()) {
            throw new BadRequestException("No Tenants with ids " + tenantIds);
        }
        presetToTenantFilter.setTenants(new ArrayList<>(map.values()));

    }

    public PaginationResponse<PresetToTenantContainer> getAllPresetToTenant(
            PresetToTenantFilter presetToTenantFilter,
            SecurityContext securityContext) {
        List<PresetToTenantContainer> list = listAllPresetToTenant(
                presetToTenantFilter, securityContext).parallelStream()
                .map(f -> new PresetToTenantContainer(f))
                .collect(Collectors.toList());
        long count = uiFieldRepository.countAllPresetToTenants(
                presetToTenantFilter, securityContext);
        return new PaginationResponse<>(list, presetToTenantFilter, count);
    }

    private List<PresetToTenant> listAllPresetToTenant(
            PresetToTenantFilter presetToTenantFilter,
            SecurityContext securityContext) {
        return uiFieldRepository.listAllPresetToTenants(presetToTenantFilter,
                securityContext);
    }

    public void validate(PresetToUserFilter presetToUserFilter,
                         SecurityContext securityContext) {
        Set<String> userIds = presetToUserFilter.getUserIds();
        Map<String, User> map = userIds.isEmpty()
                ? new HashMap<>()
                : uiFieldRepository
                .listByIds(User.class, userIds, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        userIds.removeAll(map.keySet());
        if (!userIds.isEmpty()) {
            throw new BadRequestException("No Users with ids " + userIds);
        }
        presetToUserFilter.setUsers(new ArrayList<>(map.values()));

    }

    public PaginationResponse<PresetToUserContainer> getAllPresetToUser(
            PresetToUserFilter presetToUserFilter,
            SecurityContext securityContext) {
        List<PresetToUserContainer> list = listAllPresetToUser(
                presetToUserFilter, securityContext).parallelStream()
                .map(f -> new PresetToUserContainer(f))
                .collect(Collectors.toList());
        long count = uiFieldRepository.countAllPresetToUsers(
                presetToUserFilter, securityContext);
        return new PaginationResponse<>(list, presetToUserFilter, count);
    }

    private List<PresetToUser> listAllPresetToUser(
            PresetToUserFilter presetToUserFilter,
            SecurityContext securityContext) {
        return uiFieldRepository.listAllPresetToUsers(presetToUserFilter,
                securityContext);
    }

    public PaginationResponse<UiField> getAllUiFields(
            UiFieldFiltering uiFieldFiltering, SecurityContext securityContext) {
        List<UiField> list = listAllUiFields(uiFieldFiltering, securityContext);
        long count = uiFieldRepository.countAllUiFields(uiFieldFiltering,
                securityContext);
        return new PaginationResponse<>(list, uiFieldFiltering, count);
    }

    public void validate(MassCreateUiFields massCreateUiFields,
                         SecurityContext securityContext) {
        GridPreset preset = massCreateUiFields.getGridPresetId() != null
                ? getByIdOrNull(massCreateUiFields.getGridPresetId(),
                GridPreset.class, null, securityContext) : null;
        if (preset == null) {
            throw new BadRequestException("no GridPreset with id "
                    + massCreateUiFields.getGridPresetId());
        }
        massCreateUiFields.setGridPreset(preset);
        Map<String, List<UiFieldCreate>> map = massCreateUiFields
                .getUiFields()
                .parallelStream()
                .filter(f -> f.getCategoryName() != null)
                .collect(
                        Collectors.groupingBy(f -> f.getCategoryName(),
                                Collectors.toList()));
        Map<String, Category> categoryMap = categoryService
                .listAllCategories(new CategoryFilter().setNames(map.keySet()),
                        securityContext)
                .parallelStream()
                .collect(
                        Collectors.toMap(f -> f.getName(), f -> f, (a, b) -> a));
        for (Map.Entry<String, List<UiFieldCreate>> entry : map.entrySet()) {
            Category category = categoryMap.computeIfAbsent(
                    entry.getKey(),
                    f -> categoryService.createCategory(
                            new CategoryCreate().setName(f), securityContext));
            for (UiFieldCreate createUiField : entry.getValue()) {
                createUiField.setCategory(category);
            }
        }

    }

    public void validate(UiFieldCreate createUiField,
                         SecurityContext securityContext) {
        Category category = categoryService.createCategory(
                new CategoryCreate().setName(createUiField.getCategoryName()),
                securityContext);
        createUiField.setCategory(category);
    }

    public List<UiField> massCreateUiFields(
            MassCreateUiFields massCreateUiFields,
            SecurityContext securityContext) {
        List<UiField> toMerge = new ArrayList<>();
        for (UiFieldCreate uiField : massCreateUiFields.getUiFields()) {
            toMerge.add(createUiFieldNoMerge(
                    uiField.setPreset(massCreateUiFields.getGridPreset()),
                    securityContext));
        }
        uiFieldRepository.massMerge(toMerge);
        return toMerge;
    }

    public void validate(UiFieldFiltering uiFieldFiltering,
                         SecurityContext securityContext) {
        Set<String> gridPresetIds = uiFieldFiltering.getPresetIds();
        Map<String, GridPreset> gridPresetMap = gridPresetIds.isEmpty()
                ? new HashMap<>()
                : uiFieldRepository
                .listByIds(GridPreset.class, gridPresetIds,
                        securityContext).parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        gridPresetIds.removeAll(gridPresetMap.keySet());
        if (!gridPresetIds.isEmpty()) {
            throw new BadRequestException("No Grid Presets with ids "
                    + gridPresetIds);
        }
        uiFieldFiltering.setPresets(new ArrayList<>(gridPresetMap.values()));
    }

    public UiFieldCreate getUIFieldCreate(UiField uiField) {
        return new UiFieldCreate().setPreset(uiField.getPreset())
                .setCategory(uiField.getCategory())
                .setDisplayName(uiField.getDisplayName())
                .setDescription(uiField.getDescription())
                .setVisible(uiField.isVisible())
                .setPriority(uiField.getPriority()).setName(uiField.getName());
    }

    public void validate(PreferedPresetRequest preferedPresetRequest,
                         SecurityContext securityContext) {

    }

    public List<Preset> getPreferredPresets(
            PreferedPresetRequest preferedPresetRequest,
            SecurityContext securityContext) {
        return presetToEntityService.getPreferredPresets(preferedPresetRequest,
                securityContext);
    }
}
