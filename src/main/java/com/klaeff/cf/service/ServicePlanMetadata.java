package com.klaeff.cf.service;

public class ServicePlanMetadata {

	private String[] bullets = {"10GB", "20GB"};
	private ServicePlanMetadataCost[] costs = {new ServicePlanMetadataCost(), new ServicePlanMetadataCost()};
	private String displayName = "service plan costs";

	public String[] getBullets() {
		return bullets;
	}

	public void setBullets(String[] bullets) {
		this.bullets = bullets;
	}

	public ServicePlanMetadataCost[] getCosts() {
		return costs;
	}

	public void setCosts(ServicePlanMetadataCost[] costs) {
		this.costs = costs;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}
