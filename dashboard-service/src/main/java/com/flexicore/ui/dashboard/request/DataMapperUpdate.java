package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.DataMapper;

public class DataMapperUpdate extends DataMapperCreate {

	private String id;
	@JsonIgnore
	private DataMapper dataMapper;

	public String getId() {
		return id;
	}

	public DataMapperUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public DataMapper getDataMapper() {
		return dataMapper;
	}

	public DataMapperUpdate setDataMapper(DataMapper dataMapper) {
		this.dataMapper = dataMapper;
		return this;
	}
}
