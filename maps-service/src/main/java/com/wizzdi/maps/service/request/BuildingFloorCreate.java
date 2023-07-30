package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.MappedPOI;

/**
 * Object Used to Create BuildingFloor
 */
@IdValid.List({
        @IdValid(
                targetField = "building",
                field = "buildingId",
                fieldType = Building.class,
                groups = {
                        com.wizzdi.flexicore.security.validation.Create.class,
                        com.wizzdi.flexicore.security.validation.Update.class
                }),
        @IdValid(
                targetField = "drawing",
                field = "drawingId",
                fieldType = FileResource.class,
                groups = {
                        com.wizzdi.flexicore.security.validation.Create.class,
                        com.wizzdi.flexicore.security.validation.Update.class
                })
})
public class BuildingFloorCreate extends BasicCreate {

    private String buildingId;
    @JsonIgnore
    private Building building;
    private String drawingId;
    @JsonIgnore  FileResource drawing;
    public String getBuildingId() {
        return buildingId;
    }

    public BuildingFloorCreate setBuildingId(String buildingId) {
        this.buildingId = buildingId;
        return this;
    }

    public Building getBuilding() {
        return building;
    }

    public BuildingFloorCreate setBuilding(Building building) {
        this.building = building;
        return this;
    }

    public String getDrawingId() {
        return drawingId;
    }

    public BuildingFloorCreate setDrawingId(String drawingId) {
        this.drawingId = drawingId;
        return this;
    }

    public FileResource getDrawing() {
        return drawing;
    }

    public BuildingFloorCreate setDrawing(FileResource drawing) {
        this.drawing = drawing;
        return this;
    }
}
