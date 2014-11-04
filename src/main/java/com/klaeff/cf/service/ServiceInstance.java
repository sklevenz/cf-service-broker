package com.klaeff.cf.service;

import com.google.gson.annotations.SerializedName;

public class ServiceInstance {

	@SerializedName("service_id")
	private String serviceId;
	@SerializedName("plan_id")
	private String planId;
	@SerializedName("organization_guid")
	private String organizationGuid;
	@SerializedName("space_guid")
	private String spaceGuid;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	public void setOrganizationGuid(String organizationGuid) {
		this.organizationGuid = organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ServiceInstance) {
			ServiceInstance s = (ServiceInstance) obj;
			String s1 = serviceId + planId + organizationGuid + spaceGuid;
			String s2 = s.getServiceId() + s.getPlanId() + s.getOrganizationGuid() + s.getSpaceGuid();
			
			return s1.equalsIgnoreCase(s2);
		}
		return false;
	}

}
