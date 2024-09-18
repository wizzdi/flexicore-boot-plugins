package com.wizzdi.maps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.flexicore.model.territories.Address;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "mapped_poi_idx", columnList = "layer_id,relatedId,relatedType,lat,lon,mapIcon_id"),
        @Index(name = "mapped_poi_geohash_idx", columnList = "geoHash12,geoHash11,geoHash10,geoHash9,geoHash8,geoHash7,geoHash6,geoHash5,geoHash4,geoHash3,geoHash2,geoHash1")
})
public class MappedPOI extends SecuredBasic {

    private String externalId;

    private String geoHash1;
    private String geoHash2;
    private String geoHash3;
    private String geoHash4;
    private String geoHash5;
    private String geoHash6;
    private String geoHash7;
    private String geoHash8;
    private String geoHash9;
    private String geoHash10;
    private String geoHash11;
    private String geoHash12;

    private Double x;
    private Double y;
    private Double z;

    private Double lat;
    private Double lon;

    @ManyToOne(targetEntity = MapIcon.class)
    private MapIcon mapIcon;

    @ManyToOne(targetEntity = Address.class)
    private Address address;
    @ManyToOne(targetEntity = Room.class)
    private Room room;
    @JsonIgnore
    @OneToMany(targetEntity = MapGroupToMappedPOI.class, mappedBy = "mappedPOI")
    private List<MapGroupToMappedPOI> mapGroupToMappedPOIS = new ArrayList<>();

    private boolean keepLocationHistory;
    private boolean keepStatusHistory;

    private String relatedType;
    private String relatedId;
    @ManyToOne(targetEntity = Layer.class)
    private Layer layer;

    @ManyToOne(targetEntity = Layer.class)
    public Layer getLayer() {
        return layer;
    }

    public MappedPOI setLayer(Layer layer) {
        this.layer = layer;
        return this;
    }


    /**
     * @return geoHash7
     */
    public String getGeoHash7() {
        return this.geoHash7;
    }

    /**
     * @param geoHash7 geoHash7 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash7(String geoHash7) {
        this.geoHash7 = geoHash7;
        return (T) this;
    }

    /**
     * @return geoHash10
     */
    public String getGeoHash10() {
        return this.geoHash10;
    }

    /**
     * @param geoHash10 geoHash10 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash10(String geoHash10) {
        this.geoHash10 = geoHash10;
        return (T) this;
    }

    /**
     * @return geoHash2
     */
    public String getGeoHash2() {
        return this.geoHash2;
    }

    /**
     * @param geoHash2 geoHash2 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash2(String geoHash2) {
        this.geoHash2 = geoHash2;
        return (T) this;
    }

    /**
     * @return y
     */
    public Double getY() {
        return this.y;
    }

    /**
     * @param y y to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setY(Double y) {
        this.y = y;
        return (T) this;
    }

    /**
     * @return geoHash11
     */
    public String getGeoHash11() {
        return this.geoHash11;
    }

    /**
     * @param geoHash11 geoHash11 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash11(String geoHash11) {
        this.geoHash11 = geoHash11;
        return (T) this;
    }

    /**
     * @return geoHash4
     */
    public String getGeoHash4() {
        return this.geoHash4;
    }

    /**
     * @param geoHash4 geoHash4 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash4(String geoHash4) {
        this.geoHash4 = geoHash4;
        return (T) this;
    }

    /**
     * @return geoHash6
     */
    public String getGeoHash6() {
        return this.geoHash6;
    }

    /**
     * @param geoHash6 geoHash6 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash6(String geoHash6) {
        this.geoHash6 = geoHash6;
        return (T) this;
    }

    /**
     * @return geoHash8
     */
    public String getGeoHash8() {
        return this.geoHash8;
    }

    /**
     * @param geoHash8 geoHash8 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash8(String geoHash8) {
        this.geoHash8 = geoHash8;
        return (T) this;
    }

    /**
     * @return z
     */
    public Double getZ() {
        return this.z;
    }

    /**
     * @param z z to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setZ(Double z) {
        this.z = z;
        return (T) this;
    }

    /**
     * @return geoHash1
     */
    public String getGeoHash1() {
        return this.geoHash1;
    }

    /**
     * @param geoHash1 geoHash1 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash1(String geoHash1) {
        this.geoHash1 = geoHash1;
        return (T) this;
    }

    /**
     * @return geoHash3
     */
    public String getGeoHash3() {
        return this.geoHash3;
    }

    /**
     * @param geoHash3 geoHash3 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash3(String geoHash3) {
        this.geoHash3 = geoHash3;
        return (T) this;
    }

    /**
     * @return mapIcon
     */
    @ManyToOne(targetEntity = MapIcon.class)
    public MapIcon getMapIcon() {
        return this.mapIcon;
    }

    /**
     * @param mapIcon mapIcon to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setMapIcon(MapIcon mapIcon) {
        this.mapIcon = mapIcon;
        return (T) this;
    }

    /**
     * @return geoHash9
     */
    public String getGeoHash9() {
        return this.geoHash9;
    }

    /**
     * @param geoHash9 geoHash9 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash9(String geoHash9) {
        this.geoHash9 = geoHash9;
        return (T) this;
    }

    /**
     * @return room
     */
    @ManyToOne(targetEntity = Room.class)
    public Room getRoom() {
        return this.room;
    }

    /**
     * @param room room to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setRoom(Room room) {
        this.room = room;
        return (T) this;
    }

    /**
     * @return lat
     */
    public Double getLat() {
        return this.lat;
    }

    /**
     * @param lat lat to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }

    /**
     * @return x
     */
    public Double getX() {
        return this.x;
    }

    /**
     * @param x x to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setX(Double x) {
        this.x = x;
        return (T) this;
    }

    /**
     * @return address
     */
    @ManyToOne(targetEntity = Address.class)
    public Address getAddress() {
        return this.address;
    }

    /**
     * @param address address to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setAddress(Address address) {
        this.address = address;
        return (T) this;
    }


    /**
     * @return geoHash12
     */
    public String getGeoHash12() {
        return this.geoHash12;
    }

    /**
     * @param geoHash12 geoHash12 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash12(String geoHash12) {
        this.geoHash12 = geoHash12;
        return (T) this;
    }

    /**
     * @return lon
     */
    public Double getLon() {
        return this.lon;
    }

    /**
     * @param lon lon to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    /**
     * @return geoHash5
     */
    public String getGeoHash5() {
        return this.geoHash5;
    }

    /**
     * @param geoHash5 geoHash5 to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setGeoHash5(String geoHash5) {
        this.geoHash5 = geoHash5;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = MapGroupToMappedPOI.class, mappedBy = "mappedPOI")
    public List<MapGroupToMappedPOI> getMapGroupToMappedPOIS() {
        return mapGroupToMappedPOIS;
    }

    public <T extends MappedPOI> T setMapGroupToMappedPOIS(List<MapGroupToMappedPOI> mapGroupToMappedPOIS) {
        this.mapGroupToMappedPOIS = mapGroupToMappedPOIS;
        return (T) this;
    }

    /**
     * @return externalId
     */
    public String getExternalId() {
        return this.externalId;
    }

    /**
     * @param externalId externalId to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    /**
     * @return relatedType
     */
    public String getRelatedType() {
        return this.relatedType;
    }

    /**
     * @param relatedType relatedType to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setRelatedType(String relatedType) {
        this.relatedType = relatedType;
        return (T) this;
    }

    /**
     * @return relatedId
     */
    public String getRelatedId() {
        return this.relatedId;
    }

    /**
     * @param relatedId relatedId to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setRelatedId(String relatedId) {
        this.relatedId = relatedId;
        return (T) this;
    }

    /**
     * @return keepLocationHistory
     */
    public boolean isKeepLocationHistory() {
        return this.keepLocationHistory;
    }

    /**
     * @param keepLocationHistory keepLocationHistory to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setKeepLocationHistory(boolean keepLocationHistory) {
        this.keepLocationHistory = keepLocationHistory;
        return (T) this;
    }

    /**
     * @return keepStatusHistory
     */
    public boolean isKeepStatusHistory() {
        return this.keepStatusHistory;
    }

    /**
     * @param keepStatusHistory keepStatusHistory to set
     * @return MappedPOI
     */
    public <T extends MappedPOI> T setKeepStatusHistory(boolean keepStatusHistory) {
        this.keepStatusHistory = keepStatusHistory;
        return (T) this;
    }

}
