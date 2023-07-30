package com.wizzdi.maps.service.response;

public class MappedPoiDTO {

    private final String id;
    private final String name;
    private final MapIconDTO mapIcon;
    private final Double lat;
    private final Double lon;


    public MappedPoiDTO(String id,String name, String mapIconId,String fileResourceId, double lat, double lon) {
        this.id = id;
        this.name=name;
        this.mapIcon = new MapIconDTO(mapIconId,fileResourceId);
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public MapIconDTO getMapIcon() {
        return mapIcon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }
}
