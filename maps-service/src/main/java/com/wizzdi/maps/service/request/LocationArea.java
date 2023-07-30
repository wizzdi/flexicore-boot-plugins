package com.wizzdi.maps.service.request;

public class LocationArea {

    private Double lonStart;
    private Double lonEnd;
    private Double latStart;
    private Double latEnd;


    public Double getLonStart() {
        return lonStart;
    }

    public <T extends LocationArea> T setLonStart(Double lonStart) {
        this.lonStart = lonStart;
        return (T) this;
    }

    public Double getLonEnd() {
        return lonEnd;
    }

    public <T extends LocationArea> T setLonEnd(Double lonEnd) {
        this.lonEnd = lonEnd;
        return (T) this;
    }

    public Double getLatStart() {
        return latStart;
    }

    public <T extends LocationArea> T setLatStart(Double latStart) {
        this.latStart = latStart;
        return (T) this;
    }

    public Double getLatEnd() {
        return latEnd;
    }

    public <T extends LocationArea> T setLatEnd(Double latEnd) {
        this.latEnd = latEnd;
        return (T) this;
    }
}
