package com.wizzdi.maps.service.service;

import ch.hsr.geohash.GeoHash;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.territories.Address;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.territories.service.AddressService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.*;
import com.wizzdi.maps.service.data.MappedPOIRepository;
import com.wizzdi.maps.service.request.MappedPOICreate;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.request.MappedPOIUpdate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.persistence.metamodel.SingularAttribute;

import com.wizzdi.maps.service.response.MappedPoiDTO;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class MappedPOIService implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(MappedPOIService.class);
    @Autowired
    private MappedPOIRepository repository;

    @Autowired
    private BasicService basicService;
    @Autowired
    private AddressService addressService;
    @Value("${wizzdi.mapPoi.keepLocationHistoryDefault:false}")
    private boolean keepLocationHistoryDefault;
    @Value("${wizzdi.mapPoi.keepStatusHistoryDefault:false}")
    private boolean keepStatusHistoryDefault;
    private static final Map<String, Method> setterCache = new ConcurrentHashMap<>();
    @Autowired
    @Lazy
    private ReverseGeoHashService reverseGeoHashService;

    @Autowired
    @Lazy
    private SecurityContextBase adminSecurityContext;


    /**
     * @param mappedPOICreate
     * @param securityContext
     * @return created MappedPOI
     */

    public MappedPOI createMappedPOI(
            MappedPOICreate mappedPOICreate, SecurityContextBase securityContext) {
        MappedPOI mappedPOI = createMappedPOINoMerge(mappedPOICreate, securityContext);
        this.repository.merge(mappedPOI);
        return mappedPOI;
    }

    /**
     * @param mappedPOICreate
     * @param securityContext
     * @return created MappedPOI unmerged
     */

    public MappedPOI createMappedPOINoMerge(
            MappedPOICreate mappedPOICreate, SecurityContextBase securityContext) {
        MappedPOI mappedPOI = new MappedPOI();
        mappedPOI.setId(UUID.randomUUID().toString());
        if (mappedPOICreate.getKeepLocationHistory() == null) {
            mappedPOICreate.setKeepLocationHistory(keepLocationHistoryDefault);
        }
        if (mappedPOICreate.getKeepStatusHistory() == null) {
            mappedPOICreate.setKeepStatusHistory(keepStatusHistoryDefault);
        }
        updateMappedPOINoMerge(mappedPOICreate, mappedPOI);

        BaseclassService.createSecurityObjectNoMerge(mappedPOI, securityContext);

        return mappedPOI;
    }

    /**
     * @param mappedPOICreate
     * @param mappedPOI
     * @return if mappedPOI was updated
     */

    public boolean updateMappedPOINoMerge(MappedPOICreate mappedPOICreate, MappedPOI mappedPOI) {
        boolean update = basicService.updateBasicNoMerge(mappedPOICreate, mappedPOI);
        boolean updateLocation = false;
        if (mappedPOICreate.getAddress() != null
                && (mappedPOI.getAddress() == null
                || !mappedPOICreate.getAddress().getId().equals(mappedPOI.getAddress().getId()))) {
            mappedPOI.setAddress(mappedPOICreate.getAddress());
            update = true;
        }


        if (mappedPOICreate.getLat() != null
                && (!mappedPOICreate.getLat().equals(mappedPOI.getLat()))) {
            mappedPOI.setLat(mappedPOICreate.getLat());
            update = true;
            updateLocation = true;
        }


        if (mappedPOICreate.getX() != null && (!mappedPOICreate.getX().equals(mappedPOI.getX()))) {
            mappedPOI.setX(mappedPOICreate.getX());
            updateMapLocation(mappedPOICreate, mappedPOI);
            update = true;
        }

        if (mappedPOICreate.getY() != null && (!mappedPOICreate.getY().equals(mappedPOI.getY()))) {
            mappedPOI.setY(mappedPOICreate.getY());
            updateMapLocation(mappedPOICreate, mappedPOI);
            update = true;
        }

        if (mappedPOICreate.getZ() != null && (!mappedPOICreate.getZ().equals(mappedPOI.getZ()))) {
            mappedPOI.setZ(mappedPOICreate.getZ());
            update = true;
        }

        if (mappedPOICreate.getLon() != null
                && (!mappedPOICreate.getLon().equals(mappedPOI.getLon()))) {
            mappedPOI.setLon(mappedPOICreate.getLon());
            update = true;
            updateLocation = true;

        }
        if (mappedPOICreate.getMapIcon() != null
                && (mappedPOI.getMapIcon() == null
                || !mappedPOICreate.getMapIcon().getId().equals(mappedPOI.getMapIcon().getId()))) {
            mappedPOI.setMapIcon(mappedPOICreate.getMapIcon());
            update = true;
        }
        if (mappedPOICreate.getRoom() != null
                && (mappedPOI.getRoom() == null
                || !mappedPOICreate.getRoom().getId().equals(mappedPOI.getRoom().getId()))) {
            mappedPOI.setRoom(mappedPOICreate.getRoom());
            update = true;
        }
        if (mappedPOICreate.getLayer() != null
                && (mappedPOI.getLayer() == null
                || !mappedPOICreate.getLayer().getId().equals(mappedPOI.getLayer().getId()))) {
            mappedPOI.setLayer(mappedPOICreate.getLayer());

            update = true;
        }
        if (mappedPOICreate.getExternalId() != null
                && (!mappedPOICreate.getExternalId().equals(mappedPOI.getExternalId()))) {
            mappedPOI.setExternalId(mappedPOICreate.getExternalId());
            update = true;
        }
        if (updateLocation) {
            generateGeoHash(mappedPOI);
        }

        if (mappedPOICreate.getKeepLocationHistory() != null
                && (!mappedPOICreate.getKeepLocationHistory().equals(mappedPOI.isKeepLocationHistory()))) {
            mappedPOI.setKeepLocationHistory(mappedPOICreate.getKeepLocationHistory());
            update = true;
        }
        if (mappedPOICreate.getRelatedType() != null
                && (!mappedPOICreate.getRelatedType().equals(mappedPOI.getRelatedType()))) {
            mappedPOI.setRelatedType(mappedPOICreate.getRelatedType());
            update = true;
        }

        if (mappedPOICreate.getRelatedId() != null
                && (!mappedPOICreate.getRelatedId().equals(mappedPOI.getRelatedId()))) {
            mappedPOI.setRelatedId(mappedPOICreate.getRelatedId());
            update = true;
        }
        if (mappedPOICreate.getKeepStatusHistory() != null
                && (!mappedPOICreate.getKeepStatusHistory().equals(mappedPOI.isKeepStatusHistory()))) {
            mappedPOI.setKeepStatusHistory(mappedPOICreate.getKeepStatusHistory());
            update = true;
        }


        return update;
    }

    private void updateMapLocation(MappedPOICreate mappedPOICreate, MappedPOI mappedPOI) {
        try {
            Optional<MappedPOI> buildingMappedPOI=Optional.of(mappedPOICreate).map(f->f.getRoom()).or(()->Optional.of(mappedPOI).map(f->f.getRoom())).map(f->f.getBuildingFloor()).map(f->f.getBuilding()).map(f->f.getMappedPOI());
            Double lon = buildingMappedPOI.map(f->f.getLon()).orElse(null);
            Double lat=buildingMappedPOI.map(f->f.getLat()).orElse(null);
            if (getDistance(mappedPOI, lon, lat) > 3.00) {
                if (lon != null) {
                    mappedPOI.setLon(Math.random() * 0.01 + lon);
                }
                if (lat != null) {
                    mappedPOI.setLat(Math.random() * 0.01 + lat);
                }
            }
        } catch (Exception ex) {
            logger.error("Error while updating location on a building floor");
        }
    }

    private double getDistance(MappedPOI mappedPOI, Double lon, Double lat) {
        double distance = Math.acos(Math.sin(mappedPOI.getLat()) * Math.sin(lat) + Math.cos(mappedPOI.getLat()) * Math.cos(lat) * Math.cos(mappedPOI.getLon() - lon)) * 6371;
        return distance;
    }

    public void generateGeoHash(MappedPOI mappedPOI) {
        try {
            for (int i = 1; i < 13; i++) {
                String setterName = "setGeoHash" + i;
                try {
                    String geoHash = GeoHash
                            .geoHashStringWithCharacterPrecision(
                                    mappedPOI.getLat(), mappedPOI.getLon(), i);
                    Method method = setterCache.computeIfAbsent(setterName,
                            f -> getSetterOrNull(f));
                    if (method != null) {
                        method.invoke(mappedPOI, geoHash);

                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("could not set property "
                            + setterName + " via setter");
                }

            }
        } catch (Exception e) {
            logger.error("unable to generate geo hash for mappedPOI " + mappedPOI.getId() + " (" + mappedPOI.getId() + ")");
        }
    }

    private Method getSetterOrNull(String name) {
        try {
            return MappedPOI.class.getMethod(name, String.class);
        } catch (NoSuchMethodException e) {
            logger.error("unable to get setter", e);
        }
        return null;
    }

    /**
     * @param mappedPOIUpdate
     * @param securityContext
     * @return mappedPOI
     */

    public MappedPOI updateMappedPOI(
            MappedPOIUpdate mappedPOIUpdate, SecurityContextBase securityContext) {
        MappedPOI mappedPOI = mappedPOIUpdate.getMappedPOI();
        if (updateMappedPOINoMerge(mappedPOIUpdate, mappedPOI)) {
            repository.merge(mappedPOI);
        }
        return mappedPOI;
    }

    /**
     * @param mappedPOIFilter
     * @param securityContext
     * @return PaginationResponse containing paging information for MappedPOI
     */

    public PaginationResponse<MappedPOI> getAllMappedPOIs(
            MappedPOIFilter mappedPOIFilter, SecurityContextBase securityContext) {
        List<MappedPOI> list = listAllMappedPOIs(mappedPOIFilter, securityContext);
        long count = this.repository.countAllMappedPOIs(mappedPOIFilter, securityContext);
        return new PaginationResponse<>(list, mappedPOIFilter, count);
    }

    /**
     * @param mappedPOIFilter
     * @param securityContext
     * @return List of MappedPOI
     */

    public List<MappedPOI> listAllMappedPOIs(
            MappedPOIFilter mappedPOIFilter, SecurityContextBase securityContext) {
        return repository.listAllMappedPOIs(mappedPOIFilter, securityContext);
    }

    /**
     * @param mappedPOIFilter
     * @param securityContext
     * @throws org.springframework.web.server.ResponseStatusException if mappedPOIFilter is not valid
     */

    public void validate(MappedPOIFilter mappedPOIFilter, SecurityContextBase securityContext) {
        basicService.validate(mappedPOIFilter, securityContext);
        Set<String> addressIds = mappedPOIFilter.getAddressIds();
        Map<String, Address> address = addressIds.isEmpty() ? new HashMap<>() : repository.listByIds(Address.class, addressIds, SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        addressIds.removeAll(address.keySet());
        if (!addressIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Address with ids " + addressIds);
        }
        mappedPOIFilter.setAddress(new ArrayList<>(address.values()));

        Set<String> layerIds = mappedPOIFilter.getLayerIds();
        Map<String, Layer> layerMap = layerIds.isEmpty() ? new HashMap<>() : repository.listByIds(Layer.class, layerIds, SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        layerIds.removeAll(layerMap.keySet());
        if (!layerIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Layers with ids " + layerIds);
        }
        mappedPOIFilter.setLayers(new ArrayList<>(layerMap.values()));

        Set<String> roomIds = mappedPOIFilter.getRoomIds();
        Map<String, Room> room = roomIds.isEmpty() ? new HashMap<>() : repository.listByIds(Room.class, roomIds, SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        roomIds.removeAll(room.keySet());
        if (!roomIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Room with ids " + roomIds);
        }
        mappedPOIFilter.setRoom(new ArrayList<>(room.values()));

        Set<String> buildingFloorIds = mappedPOIFilter.getBuildingFloorIds();
        Map<String, BuildingFloor> buildingFloorMap = buildingFloorIds.isEmpty() ? new HashMap<>() : repository.listByIds(BuildingFloor.class, buildingFloorIds, SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        buildingFloorIds.removeAll(buildingFloorMap.keySet());
        if (!buildingFloorIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No BuildingFloor with ids " + buildingFloorIds);
        }
        mappedPOIFilter.setBuildingFloors(new ArrayList<>(buildingFloorMap.values()));

        Set<String> mapGroupIds = mappedPOIFilter.getMapGroupIds();
        Map<String, MapGroup> mapGroups = mapGroupIds.isEmpty() ? new HashMap<>() : repository.listByIds(MapGroup.class, mapGroupIds, SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        mapGroupIds.removeAll(mapGroups.keySet());
        if (!mapGroupIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MapGroup with ids " + mapGroupIds);
        }
        mappedPOIFilter.setMapGroups(new ArrayList<>(mapGroups.values()));

        Set<String> mapIconIds = mappedPOIFilter.getMapIconsIds();
        Map<String, MapIcon> mapIconMap = mapIconIds.isEmpty() ? new HashMap<>() : repository.listByIds(MapIcon.class, mapIconIds, SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        mapIconIds.removeAll(mapIconMap.keySet());
        if (!mapIconIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MapIcon with ids " + mapIconIds);
        }
        mappedPOIFilter.setMapIcons(new ArrayList<>(mapIconMap.values()));


        if (mappedPOIFilter.getAddressFilter() != null) {
            addressService.validate(mappedPOIFilter.getAddressFilter(), securityContext);
        }
        Set<String> tenantIds = mappedPOIFilter.getTenantIds();
        Map<String, SecurityTenant> securityTenantMap = tenantIds.isEmpty() ? new HashMap<>() : this.repository.listByIds(SecurityTenant.class, tenantIds,SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
        tenantIds.removeAll(securityTenantMap.keySet());
        if (!tenantIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No SecurityTenant with ids " + tenantIds);
        }
        mappedPOIFilter.setTenants(new ArrayList<>(securityTenantMap.values()));


    }

    /**
     * @param mappedPOICreate
     * @param securityContext
     * @throws org.springframework.web.server.ResponseStatusException if mappedPOICreate is not valid
     */

    public void validate(MappedPOICreate mappedPOICreate, SecurityContextBase securityContext) {
        basicService.validate(mappedPOICreate, securityContext);

        String mapIconId = mappedPOICreate.getMapIconId();
        MapIcon mapIcon = mapIconId == null ? null : this.repository.getByIdOrNull(mapIconId, MapIcon.class, SecuredBasic_.security, securityContext);
        if (mapIconId != null && mapIcon == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No MapIcon with id " + mapIconId);
        }
        mappedPOICreate.setMapIcon(mapIcon);

        String layerId = mappedPOICreate.getLayerId();
        Layer layer = layerId == null ? null : this.repository.getByIdOrNull(layerId, Layer.class, SecuredBasic_.security, securityContext);
        if (layerId != null && layer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Layer with id " + layerId);
        }
        mappedPOICreate.setLayer(layer);


        String addressId = mappedPOICreate.getAddressId();
        Address address = addressId == null ? null : repository.getByIdOrNull(addressId, Address.class, SecuredBasic_.security, securityContext);
        if (addressId != null && address == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Address with id " + addressId);
            }
            mappedPOICreate.setAddress(address);


        String roomId = mappedPOICreate.getRoomId();
        Room room = roomId == null ? null : this.repository.getByIdOrNull(roomId, Room.class, SecuredBasic_.security, securityContext);
        if (roomId != null && room == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Room with id " + roomId);
            }
            mappedPOICreate.setRoom(room);


        if (mappedPOICreate.getAddress() == null && mappedPOICreate.getLat() != null && mappedPOICreate.getLon() != null) {
            try {
                Address reverseAddress = reverseGeoHashService.getAddress(mappedPOICreate.getLat(), mappedPOICreate.getLon(), adminSecurityContext);
                mappedPOICreate.setAddress(reverseAddress);
            } catch (Throwable e) {
                logger.error("failed calculating reverse geohash", e);
            }
        }
    }


    public <T extends Baseclass> List<T> listByIds(
            Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }


    public <T extends Baseclass> T getByIdOrNull(
            String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }


    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
            String id,
            Class<T> c,
            SingularAttribute<D, E> baseclassAttribute,
            SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }


    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
            Class<T> c,
            Set<String> ids,
            SingularAttribute<D, E> baseclassAttribute,
            SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }


    public <D extends Basic, T extends D> List<T> findByIds(
            Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }


    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }


    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }


    public void merge(java.lang.Object base) {
        repository.merge(base);
    }


    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }


    public PaginationResponse<MappedPoiDTO> getAllMappedPOIDTOs(MappedPOIFilter mappedPOIFilter, SecurityContextBase securityContext) {
        List<MappedPoiDTO> mappedPoiDTOS = repository.listAllMappedPOIDTOs(mappedPOIFilter, securityContext);
        long count = repository.countAllMappedPOIs(mappedPOIFilter, securityContext);
        return new PaginationResponse<>(mappedPoiDTOS, mappedPOIFilter, count);
    }
}
