package com.wizzdi.maps.service.response;

public class MapIconDTO {

    private final String id;
    private final String fileResourceId;

    public MapIconDTO(String id, String fileResourceId) {
        this.id = id;
        this.fileResourceId = fileResourceId;
    }

    public String getId() {
        return id;
    }

    public String getFileResourceId() {
        return fileResourceId;
    }
}
