package com.klaeff.cf.service;

import java.util.UUID;

public class ServicePlan {
	private UUID id = UUID.randomUUID();
	private String name;
	private String description;
	private ServicePlanMetadata metadata;

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServicePlanMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ServicePlanMetadata metadata) {
		this.metadata = metadata;
	}
}
