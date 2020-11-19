package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.GraphTemplate;

public class GraphTemplateUpdate extends GraphTemplateCreate {

	private String id;
	@JsonIgnore
	private GraphTemplate graphTemplate;

	public String getId() {
		return id;
	}

	public GraphTemplateUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public GraphTemplate getGraphTemplate() {
		return graphTemplate;
	}

	public GraphTemplateUpdate setGraphTemplate(GraphTemplate graphTemplate) {
		this.graphTemplate = graphTemplate;
		return this;
	}
}
