package com.flexicore.ui.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.ui.data.GridPresetStyleRepository;
import com.flexicore.ui.model.GridPresetStyle;
import com.flexicore.ui.request.GridPresetStyleCreate;
import com.flexicore.ui.request.GridPresetStyleFiltering;
import com.flexicore.ui.request.GridPresetStyleUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Extension
@Component
public class GridPresetStyleService implements Plugin {

    @Autowired
    private GridPresetStyleRepository gridPresetStyleRepository;
    @Autowired
    private BasicService basicService;

    public PaginationResponse<GridPresetStyle> getAllGridPresetStyles(
            GridPresetStyleFiltering filtering,
            SecurityContext securityContext) {
        List<GridPresetStyle> list = listAllGridPresetStyles(filtering, securityContext);
        long count = gridPresetStyleRepository.countAllGridPresetStyles(filtering, securityContext);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<GridPresetStyle> listAllGridPresetStyles(
            GridPresetStyleFiltering filtering,
            SecurityContext securityContext) {
        return gridPresetStyleRepository.listAllGridPresetStyles(filtering, securityContext);
    }

    @Transactional
    public GridPresetStyle createGridPresetStyle(GridPresetStyleCreate create,
                                                  SecurityContext securityContext) {
        GridPresetStyle style = createGridPresetStyleNoMerge(create, securityContext);
        gridPresetStyleRepository.merge(style);
        return style;
    }

    public GridPresetStyle createGridPresetStyleNoMerge(GridPresetStyleCreate create,
                                                         SecurityContext securityContext) {
        GridPresetStyle style = new GridPresetStyle();
        style.setId(UUID.randomUUID().toString());
        updateGridPresetStyleNoMerge(create, style);
        BaseclassService.createSecurityObjectNoMerge(style, securityContext);
        return style;
    }

    @Transactional
    public GridPresetStyle updateGridPresetStyle(GridPresetStyleUpdate update,
                                                  SecurityContext securityContext) {
        GridPresetStyle style = update.getGridPresetStyle();
        if (updateGridPresetStyleNoMerge(update, style)) {
            gridPresetStyleRepository.merge(style);
        }
        return style;
    }

    public boolean updateGridPresetStyleNoMerge(GridPresetStyleCreate create,
                                                 GridPresetStyle style) {
        boolean changed = basicService.updateBasicNoMerge(create, style);
        if (create.getTitleStyle() != null && !Objects.equals(create.getTitleStyle(), style.getTitleStyle())) {
            style.setTitleStyle(create.getTitleStyle());
            changed = true;
        }
        if (create.getTableStyle() != null && !Objects.equals(create.getTableStyle(), style.getTableStyle())) {
            style.setTableStyle(create.getTableStyle());
            changed = true;
        }
        return changed;
    }

    public void validate(GridPresetStyleCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        String effectiveName = create.getName();
        if (effectiveName == null && create instanceof GridPresetStyleUpdate update && update.getGridPresetStyle() != null) {
            effectiveName = update.getGridPresetStyle().getName();
        }
        if (effectiveName == null || effectiveName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GridPresetStyle name must be provided");
        }
    }

    public void validateFiltering(GridPresetStyleFiltering filtering, SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);
        if (filtering.getBasicPropertiesFilter() != null) {
            basicService.validate(filtering.getBasicPropertiesFilter(), securityContext);
        }
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> type, Set<String> ids,
                                                    SecurityContext securityContext) {
        return gridPresetStyleRepository.listByIds(type, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type,
                                                 SecurityContext securityContext) {
        return gridPresetStyleRepository.getByIdOrNull(id, type, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
            String id, Class<T> type, SingularAttribute<D, E> baseclassAttribute,
            SecurityContext securityContext) {
        return gridPresetStyleRepository.getByIdOrNull(id, type, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
            Class<T> type, Set<String> ids, SingularAttribute<D, E> baseclassAttribute,
            SecurityContext securityContext) {
        return gridPresetStyleRepository.listByIds(type, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(
            Class<T> type, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return gridPresetStyleRepository.findByIds(type, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> type, Set<String> ids) {
        return gridPresetStyleRepository.findByIds(type, ids);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return gridPresetStyleRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        gridPresetStyleRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        gridPresetStyleRepository.massMerge(toMerge);
    }
}
