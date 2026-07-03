package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.Layer;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOIToLayer;
import com.wizzdi.maps.service.data.MappedPOIToLayerRepository;
import com.wizzdi.maps.service.request.MappedPOIToLayerCreate;
import com.wizzdi.maps.service.request.MappedPOIToLayerFilter;
import com.wizzdi.maps.service.request.MappedPOIToLayerUpdate;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Extension
public class MappedPOIToLayerService implements Plugin {

    @Autowired
    private MappedPOIToLayerRepository repository;
    @Autowired
    private BasicService basicService;

    public MappedPOIToLayer createMappedPOIToLayer(MappedPOIToLayerCreate create, SecurityContext securityContext) {
        MappedPOIToLayer mappedPOIToLayer = createMappedPOIToLayerNoMerge(create, securityContext);
        repository.merge(mappedPOIToLayer);
        return mappedPOIToLayer;
    }

    public MappedPOIToLayer createMappedPOIToLayerNoMerge(MappedPOIToLayerCreate create, SecurityContext securityContext) {
        MappedPOIToLayer mappedPOIToLayer = new MappedPOIToLayer();
        mappedPOIToLayer.setId(UUID.randomUUID().toString());
        updateMappedPOIToLayerNoMerge(mappedPOIToLayer, create);
        BaseclassService.createSecurityObjectNoMerge(mappedPOIToLayer, securityContext);
        return mappedPOIToLayer;
    }

    public boolean updateMappedPOIToLayerNoMerge(MappedPOIToLayer mappedPOIToLayer, MappedPOIToLayerCreate create) {
        boolean update = basicService.updateBasicNoMerge(create, mappedPOIToLayer);
        if (create.getMappedPOI() != null && (mappedPOIToLayer.getMappedPOI() == null || !create.getMappedPOI().getId().equals(mappedPOIToLayer.getMappedPOI().getId()))) {
            mappedPOIToLayer.setMappedPOI(create.getMappedPOI());
            update = true;
        }
        if (create.getLayer() != null && (mappedPOIToLayer.getLayer() == null || !create.getLayer().getId().equals(mappedPOIToLayer.getLayer().getId()))) {
            mappedPOIToLayer.setLayer(create.getLayer());
            update = true;
        }
        return update;
    }

    public MappedPOIToLayer updateMappedPOIToLayer(MappedPOIToLayerUpdate update, SecurityContext securityContext) {
        MappedPOIToLayer mappedPOIToLayer = update.getMappedPOIToLayer();
        if (updateMappedPOIToLayerNoMerge(mappedPOIToLayer, update)) {
            repository.merge(mappedPOIToLayer);
        }
        return mappedPOIToLayer;
    }

    public PaginationResponse<MappedPOIToLayer> getAllMappedPOIToLayers(MappedPOIToLayerFilter filter, SecurityContext securityContext) {
        List<MappedPOIToLayer> list = listAllMappedPOIToLayers(filter, securityContext);
        long count = repository.countAllMappedPOIToLayers(filter, securityContext);
        return new PaginationResponse<>(list, filter, count);
    }

    public List<MappedPOIToLayer> listAllMappedPOIToLayers(MappedPOIToLayerFilter filter, SecurityContext securityContext) {
        return repository.listAllMappedPOIToLayers(filter, securityContext);
    }

    public void validate(MappedPOIToLayerCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);

        String mappedPOIId = create.getMappedPOIId();
        MappedPOI mappedPOI = mappedPOIId == null ? null : repository.getByIdOrNull(mappedPOIId, MappedPOI.class, securityContext);
        if (mappedPOIId != null && mappedPOI == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MappedPOI with id " + mappedPOIId);
        }
        create.setMappedPOI(mappedPOI);

        String layerId = create.getLayerId();
        Layer layer = layerId == null ? null : repository.getByIdOrNull(layerId, Layer.class, securityContext);
        if (layerId != null && layer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Layer with id " + layerId);
        }
        create.setLayer(layer);
    }

    public void validate(MappedPOIToLayerUpdate update, SecurityContext securityContext) {
        validate((MappedPOIToLayerCreate) update, securityContext);
        String id = update.getId();
        MappedPOIToLayer mappedPOIToLayer = id == null ? null : repository.getByIdOrNull(id, MappedPOIToLayer.class, securityContext);
        if (id != null && mappedPOIToLayer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MappedPOIToLayer with id " + id);
        }
        update.setMappedPOIToLayer(mappedPOIToLayer);
    }

    public void validate(MappedPOIToLayerFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);

        Set<String> mappedPOIIds = filter.getMappedPOIIds();
        Map<String, MappedPOI> mappedPOIMap = mappedPOIIds == null || mappedPOIIds.isEmpty() ? new HashMap<>() : repository.listByIds(MappedPOI.class, mappedPOIIds, securityContext).stream().collect(Collectors.toMap(MappedPOI::getId, f -> f));
        if (mappedPOIIds != null) {
            mappedPOIIds.removeAll(mappedPOIMap.keySet());
            if (!mappedPOIIds.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MappedPOI with ids " + mappedPOIIds);
            }
        }
        filter.setMappedPOIs(new ArrayList<>(mappedPOIMap.values()));

        Set<String> layerIds = filter.getLayerIds();
        Map<String, Layer> layerMap = layerIds == null || layerIds.isEmpty() ? new HashMap<>() : repository.listByIds(Layer.class, layerIds, securityContext).stream().collect(Collectors.toMap(Layer::getId, f -> f));
        if (layerIds != null) {
            layerIds.removeAll(layerMap.keySet());
            if (!layerIds.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Layer with ids " + layerIds);
            }
        }
        filter.setLayers(new ArrayList<>(layerMap.values()));
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    public void merge(Object base) {
        repository.merge(base);
    }

    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }
}
