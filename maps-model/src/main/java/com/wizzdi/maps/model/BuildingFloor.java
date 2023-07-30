package com.wizzdi.maps.model;

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
    @ManyToOne(targetEntity = Building.class)
    private Building building;
    @ManyToOne(targetEntity = FileResource.class)
    private FileResource drawing;
    @OneToMany(targetEntity = MappedPOI.class,mappedBy = "buildingFloor")
    private List<MappedPOI> mappedPOIs=new ArrayList<>();

    public Building getBuilding() {
        return building;
    }

    public BuildingFloor setBuilding(Building building) {
        this.building = building;
        return this;
    }

    public FileResource getDrawing() {
        return drawing;
    }

    public BuildingFloor setDrawing(FileResource drawing) {
        this.drawing = drawing;
        return this;
    }

    public List<MappedPOI> getMappedPOIs() {
        return mappedPOIs;
    }

    public BuildingFloor setMappedPOIs(List<MappedPOI> mappedPOIs) {
        this.mappedPOIs = mappedPOIs;
        return this;
    }
}
