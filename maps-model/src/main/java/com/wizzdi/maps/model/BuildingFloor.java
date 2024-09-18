package com.wizzdi.maps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.file.model.FileResource;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "buildingFloor_idx",columnList = "building_id")
})
public class BuildingFloor extends SecuredBasic {

    private String externalId;
    @JoinColumn(nullable = false)
    @ManyToOne(targetEntity = Building.class)
    private Building building;
    @ManyToOne(targetEntity = FileResource.class)
    private FileResource drawing;

    @ManyToOne(targetEntity = Building.class)
    public Building getBuilding() {
        return building;
    }

    public BuildingFloor setBuilding(Building building) {
        this.building = building;
        return this;
    }

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getDrawing() {
        return drawing;
    }

    public BuildingFloor setDrawing(FileResource drawing) {
        this.drawing = drawing;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends BuildingFloor> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
