package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.territories.Address;
import com.flexicore.territories.request.AddressFilter;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.maps.model.*;

import com.wizzdi.maps.model.Room;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Object Used to List MappedPOI
 */
//@com.wizzdi.flexicore.security.validation.IdValid.List({
//        @IdValid(
//                targetField = "address",
//                field = "addressIds",
//                fieldType = com.flexicore.model.territories.Address.class),
//        @IdValid(
//                targetField = "mapIcons",
//                field = "mapIconsIds",
//                fieldType = com.wizzdi.maps.model.MapIcon.class),
//        @IdValid(targetField = "room", field = "roomIds", fieldType = com.wizzdi.maps.model.Room.class),
//         @IdValid(
//                 targetField = "buildingFloors",
//                 field = "buildingFloorIds",
//                 fieldType = com.wizzdi.maps.model.BuildingFloor.class),
//        @IdValid(
//                targetField = "layers",
//                field = "layerIds",
//                fieldType = com.wizzdi.maps.model.Layer.class)
//})
public class MappedPOIFilter extends PaginationFilter {


    private Boolean inBuilding;
    private Set<String> addressIds = new HashSet<>();
    @JsonIgnore
    private List<Address> address;
    private boolean addressExclude;
    private BasicPropertiesFilter basicPropertiesFilter;
    private LocationArea locationArea;
    @JsonIgnore
    private List<Room> room;
    private boolean roomExclude;
    private boolean buildingFloorExclude;
    private Set<String> buildingFloorIds;
    @JsonIgnore
    @TypeRetention(BuildingFloor.class)
    private List<BuildingFloor> buildingFloors;
    private Set<String> roomIds = new HashSet<>();
    private MapGroupFilter mapGroupFilter;
    private Set<String> externalId;
    private String externalIdLike;
    private boolean externalIdExclude;
    private Set<String> mapGroupIds = new HashSet<>();
    @JsonIgnore
    @TypeRetention(MapGroup.class)
    private List<MapGroup> mapGroups;

    private boolean mapGroupExclude;

    private Set<String> relatedType;
    private boolean relatedTypeExclude;

    private Set<String> relatedId;
    private boolean relatedIdExclude;

    private AddressFilter addressFilter;
    @JsonIgnore
    private List<MapIcon> mapIcons;
    private boolean mapIconsExclude;

    private Set<String> mapIconsIds = new HashSet<>();
    @JsonIgnore
    private List<SecurityTenant> tenants;
    private boolean tenantsExclude;

    private Set<String> tenantIds = new HashSet<>();

    @JsonIgnore
    private PredicateAdder<MappedPOIFilter> predicateAdder;
    private Set<String> layerIds;
    @JsonIgnore
    @TypeRetention(Layer.class)
    private List<Layer> layers;
    private boolean layerExclude;

    private Boolean withIcon;

    private Boolean hasLocation;

    public MappedPOIFilter() {
    }

    public MappedPOIFilter(MappedPOIFilter other) {
        this.addressIds = other.addressIds;
        this.address = other.address;
        this.addressExclude = other.addressExclude;
        this.basicPropertiesFilter = other.basicPropertiesFilter;
        this.locationArea = other.locationArea;
        this.room = other.room;
        this.roomExclude = other.roomExclude;
        this.roomIds = other.roomIds;
        this.mapGroupFilter = other.mapGroupFilter;
        this.externalId = other.externalId;
        this.externalIdExclude = other.externalIdExclude;
        this.relatedType = other.relatedType;
        this.relatedTypeExclude = other.relatedTypeExclude;
        this.relatedId = other.relatedId;
        this.relatedIdExclude = other.relatedIdExclude;
        this.addressFilter = other.addressFilter;
        this.mapIcons = other.mapIcons;
        this.mapIconsExclude = other.mapIconsExclude;
        this.mapIconsIds = other.mapIconsIds;
        this.tenants = other.tenants;
        this.tenantsExclude = other.tenantsExclude;
        this.tenantIds = other.tenantIds;
        this.predicateAdder = other.predicateAdder;
        this.externalIdLike = other.externalIdLike;
        this.inBuilding =other.inBuilding;
        this.mapGroups=other.mapGroups;
        this.mapGroupIds=other.mapGroupIds;
        this.mapGroupExclude=other.mapGroupExclude;
        this.buildingFloorExclude=other.buildingFloorExclude;
        this.buildingFloorIds=other.buildingFloorIds;
        this.buildingFloors=other.buildingFloors;
        this.layerExclude=other.layerExclude;
        this.layerIds=other.layerIds;
        this.layers=other.layers;
        this.withIcon=other.withIcon;
        this.hasLocation=other.hasLocation;
    }

    public Set<String> getAddressIds() {
        return addressIds;
    }

    public <T extends MappedPOIFilter> T setAddressIds(Set<String> addressIds) {
        this.addressIds = addressIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Room> getRoom() {
        return this.room;
    }

    public <T extends MappedPOIFilter> T setRoom(List<Room> room) {
        this.room = room;
        return (T) this;
    }

    public Set<String> getRoomIds() {
        return this.roomIds;
    }

    public <T extends MappedPOIFilter> T setRoomIds(Set<String> roomIds) {
        this.roomIds = roomIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Address> getAddress() {
        return address;
    }

    public <T extends MappedPOIFilter> T setAddress(List<Address> address) {
        this.address = address;
        return (T) this;
    }

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends MappedPOIFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public LocationArea getLocationArea() {
        return locationArea;
    }

    public <T extends MappedPOIFilter> T setLocationArea(LocationArea locationArea) {
        this.locationArea = locationArea;
        return (T) this;
    }

    public MapGroupFilter getMapGroupFilter() {
        return mapGroupFilter;
    }

    public <T extends MappedPOIFilter> T setMapGroupFilter(MapGroupFilter mapGroupFilter) {
        this.mapGroupFilter = mapGroupFilter;
        return (T) this;
    }

    public Set<String> getExternalId() {
        return this.externalId;
    }

    public <T extends MappedPOIFilter> T setExternalId(Set<String> externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    /**
     * @return relatedType
     */
    public Set<String> getRelatedType() {
        return this.relatedType;
    }

    /**
     * @param relatedType relatedType to set
     * @return MappedPOIFilter
     */
    public <T extends MappedPOIFilter> T setRelatedType(Set<String> relatedType) {
        this.relatedType = relatedType;
        return (T) this;
    }

    /**
     * @return relatedId
     */
    public Set<String> getRelatedId() {
        return this.relatedId;
    }

    /**
     * @param relatedId relatedId to set
     * @return MappedPOIFilter
     */
    public <T extends MappedPOIFilter> T setRelatedId(Set<String> relatedId) {
        this.relatedId = relatedId;
        return (T) this;
    }

    public AddressFilter getAddressFilter() {
        return addressFilter;
    }

    public <T extends MappedPOIFilter> T setAddressFilter(AddressFilter addressFilter) {
        this.addressFilter = addressFilter;
        return (T) this;
    }

    @JsonIgnore
    public List<MapIcon> getMapIcons() {
        return mapIcons;
    }

    public <T extends MappedPOIFilter> T setMapIcons(List<MapIcon> mapIcons) {
        this.mapIcons = mapIcons;
        return (T) this;
    }

    public Set<String> getMapIconsIds() {
        return mapIconsIds;
    }

    public <T extends MappedPOIFilter> T setMapIconsIds(Set<String> mapIconsIds) {
        this.mapIconsIds = mapIconsIds;
        return (T) this;
    }

    @JsonIgnore
    public List<SecurityTenant> getTenants() {
        return tenants;
    }

    public <T extends MappedPOIFilter> T setTenants(List<SecurityTenant> tenants) {
        this.tenants = tenants;
        return (T) this;
    }

    public Set<String> getTenantIds() {
        return tenantIds;
    }

    public <T extends MappedPOIFilter> T setTenantIds(Set<String> tenantIds) {
        this.tenantIds = tenantIds;
        return (T) this;
    }

    public boolean isAddressExclude() {
        return addressExclude;
    }

    public <T extends MappedPOIFilter> T setAddressExclude(boolean addressExclude) {
        this.addressExclude = addressExclude;
        return (T) this;
    }

    public boolean isRoomExclude() {
        return roomExclude;
    }

    public <T extends MappedPOIFilter> T setRoomExclude(boolean roomExclude) {
        this.roomExclude = roomExclude;
        return (T) this;
    }

    public boolean isExternalIdExclude() {
        return externalIdExclude;
    }

    public <T extends MappedPOIFilter> T setExternalIdExclude(boolean externalIdExclude) {
        this.externalIdExclude = externalIdExclude;
        return (T) this;
    }

    public boolean isRelatedTypeExclude() {
        return relatedTypeExclude;
    }

    public <T extends MappedPOIFilter> T setRelatedTypeExclude(boolean relatedTypeExclude) {
        this.relatedTypeExclude = relatedTypeExclude;
        return (T) this;
    }

    public boolean isRelatedIdExclude() {
        return relatedIdExclude;
    }

    public <T extends MappedPOIFilter> T setRelatedIdExclude(boolean relatedIdExclude) {
        this.relatedIdExclude = relatedIdExclude;
        return (T) this;
    }

    public boolean isMapIconsExclude() {
        return mapIconsExclude;
    }

    public <T extends MappedPOIFilter> T setMapIconsExclude(boolean mapIconsExclude) {
        this.mapIconsExclude = mapIconsExclude;
        return (T) this;
    }

    public boolean isTenantsExclude() {
        return tenantsExclude;
    }

    public <T extends MappedPOIFilter> T setTenantsExclude(boolean tenantsExclude) {
        this.tenantsExclude = tenantsExclude;
        return (T) this;
    }

    @JsonIgnore
    public PredicateAdder<MappedPOIFilter> getPredicateAdder() {
        return predicateAdder;
    }

    public <T extends MappedPOIFilter> T setPredicateAdder(PredicateAdder<MappedPOIFilter> predicateAdder) {
        this.predicateAdder = predicateAdder;
        return (T) this;
    }

    public String getExternalIdLike() {
        return externalIdLike;
    }

    public <T extends MappedPOIFilter> T setExternalIdLike(String externalIdLike) {
        this.externalIdLike = externalIdLike;
        return (T) this;
    }

  public Set<String> getBuildingFloorIds() {
    return buildingFloorIds;
  }

  public MappedPOIFilter setBuildingFloorIds(Set<String> buildingFloorIds) {
    this.buildingFloorIds = buildingFloorIds;
    return this;
  }

  public List<BuildingFloor> getBuildingFloors() {
        return buildingFloors;
    }

    public MappedPOIFilter setBuildingFloors(List<BuildingFloor> buildingFloors) {
        this.buildingFloors = buildingFloors;
        return this;
    }

    public boolean isBuildingFloorExclude() {
        return buildingFloorExclude;
    }

    public MappedPOIFilter setBuildingFloorExclude(boolean buildingFloorExclude) {
        this.buildingFloorExclude = buildingFloorExclude;
        return this;
    }

    public Set<String> getLayerIds() {
        return layerIds;
    }

    public MappedPOIFilter setLayerIds(Set<String> layerIds) {
        this.layerIds = layerIds;
        return this;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public MappedPOIFilter setLayers(List<Layer> layers) {
        this.layers = layers;
        return this;
    }

    public boolean isLayerExclude() {
        return layerExclude;
    }

    public MappedPOIFilter setLayerExclude(boolean layerExclude) {
        this.layerExclude = layerExclude;
        return this;
    }

    public Boolean getInBuilding() {
        return inBuilding;
    }

    public <T extends MappedPOIFilter> T setInBuilding(Boolean inBuilding) {
        this.inBuilding = inBuilding;
        return (T) this;
    }

    public Set<String> getMapGroupIds() {
        return mapGroupIds;
    }

    public <T extends MappedPOIFilter> T setMapGroupIds(Set<String> mapGroupIds) {
        this.mapGroupIds = mapGroupIds;
        return (T) this;
    }

    @JsonIgnore
    @TypeRetention(MapGroup.class)
    public List<MapGroup> getMapGroups() {
        return mapGroups;
    }

    public <T extends MappedPOIFilter> T setMapGroups(List<MapGroup> mapGroups) {
        this.mapGroups = mapGroups;
        return (T) this;
    }

    public boolean isMapGroupExclude() {
        return mapGroupExclude;
    }

    public <T extends MappedPOIFilter> T setMapGroupExclude(boolean mapGroupExclude) {
        this.mapGroupExclude = mapGroupExclude;
        return (T) this;
    }

    public Boolean getWithIcon() {
        return withIcon;
    }

    public void setWithIcon(Boolean withIcon) {
        this.withIcon = withIcon;
    }

    public Boolean getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(Boolean hasLocation) {
        this.hasLocation = hasLocation;
    }
}
