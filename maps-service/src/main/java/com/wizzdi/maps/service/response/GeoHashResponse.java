package com.wizzdi.maps.service.response;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;

public class GeoHashResponse {

	private double latMin;
	private double lonMin;
	private double latMax;
	private double lonMax;
	private Long count;

	public GeoHashResponse() {
	}

	public GeoHashResponse(String geoHash, Long count) {
		BoundingBox point = geoHash != null ? GeoHash
				.fromGeohashString(geoHash).getBoundingBox() : null;
		latMin = point != null ? point.getSouthLatitude() : -1d;
		lonMin = point != null ? point.getWestLongitude() : -1d;
		latMax = point != null ? point.getNorthLatitude() : -1d;
		lonMax = point != null ? point.getEastLongitude() : -1d;
		this.count = count;
	}

	public Long getCount() {
		return count;
	}

	public GeoHashResponse setCount(Long count) {
		this.count = count;
		return this;
	}

	public double getLatMin() {
		return latMin;
	}

	public GeoHashResponse setLatMin(double latMin) {
		this.latMin = latMin;
		return this;
	}

	public double getLonMin() {
		return lonMin;
	}

	public GeoHashResponse setLonMin(double lonMin) {
		this.lonMin = lonMin;
		return this;
	}

	public double getLatMax() {
		return latMax;
	}

	public GeoHashResponse setLatMax(double latMax) {
		this.latMax = latMax;
		return this;
	}

	public double getLonMax() {
		return lonMax;
	}

	public GeoHashResponse setLonMax(double lonMax) {
		this.lonMax = lonMax;
		return this;
	}
}
