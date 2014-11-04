package com.klaeff.cf.service;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class Service {

	private UUID id = UUID.randomUUID();
	private String name = "mock service";
	private String description = "This broker is for a mocking service which records CF broker requests in memory.";
	private boolean bindable = true;
	private String[] tags = { "tag1", "tag2" };
	private String [] requires = {};
	
	@SerializedName("dashboard_client")
	private ServiceDashboardClient dashboardClient = new ServiceDashboardClient();
	private ServicePlan[] plans = { new ServicePlan(), new ServicePlan() };
	private ServiceMetadata metadata = new ServiceMetadata();

	{
		plans[0].setName("small");
		plans[0].setDescription("A small plan");

		ServicePlanMetadata spm0 = new ServicePlanMetadata();
		plans[0].setMetadata(spm0);

		plans[1].setName("large");
		plans[1].setDescription("A large plan");

		ServicePlanMetadata spm1 = new ServicePlanMetadata();
		plans[1].setMetadata(spm1);
	}

	public ServiceMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ServiceMetadata metadata) {
		this.metadata = metadata;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

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

	public boolean isBindable() {
		return bindable;
	}

	public void setBindable(boolean bindable) {
		this.bindable = bindable;
	}

	public ServicePlan[] getPlans() {
		return plans;
	}

	public void setPlans(ServicePlan[] plans) {
		this.plans = plans;
	}

	public ServiceDashboardClient getDashboardClient() {
		return dashboardClient;
	}

	public void setDashboardClient(ServiceDashboardClient dashboardClient) {
		this.dashboardClient = dashboardClient;
	}

	public String [] getRequires() {
		return requires;
	}

	public void setRequires(String [] requires) {
		this.requires = requires;
	}
}
